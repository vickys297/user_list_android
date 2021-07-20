package com.example.randomuserlisting.ui.fragments.userList

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.randomuserlisting.MyApplication
import com.example.randomuserlisting.R
import com.example.randomuserlisting.adapters.UserListAdapter
import com.example.randomuserlisting.adapters.UserListLoadStateAdapter
import com.example.randomuserlisting.databinding.UserListFragmentBinding
import com.example.randomuserlisting.localDatabase.AppDatabase
import com.example.randomuserlisting.model.UserModel
import com.example.randomuserlisting.model.WeatherModel
import com.example.randomuserlisting.model.WeatherResponse
import com.example.randomuserlisting.network.NetworkHelperClass
import com.example.randomuserlisting.network.NoConnectivityException
import com.example.randomuserlisting.utils.AppConstants
import com.example.randomuserlisting.utils.AppInterface
import com.example.randomuserlisting.utils.AppRepository
import com.example.randomuserlisting.utils.AppViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException


@ExperimentalPagingApi
private val TAG = UserList::class.java.canonicalName

@ExperimentalPagingApi
class UserList : Fragment(R.layout.user_list_fragment) {

    private var requestingLocationUpdates: Boolean = false
    private val REQUEST_CHECK_SETTINGS: Int = 10001

    private lateinit var _binding: UserListFragmentBinding
    private val binding: UserListFragmentBinding get() = _binding

    private lateinit var viewModel: UserListViewModel

    private lateinit var networkHelperClass: NetworkHelperClass

    private lateinit var userListAdapter: UserListAdapter
    private lateinit var userSearchListAdapter: UserListAdapter

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var locationManager: LocationManager


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest


    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationString = locationResult.lastLocation

            Log.i(TAG, "onLocationResult: $locationString")
//            displayWeatherDetails(locationString)
        }
    }


    private val userListInterface = object : AppInterface.UserList {
        override fun onUserClick(item: UserModel) {
            val bundle = Bundle()
            bundle.putString(AppConstants.Navigation.USER_KEY, Gson().toJson(item))
            findNavController().navigate(R.id.action_userList_to_userDetails, bundle)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = UserListFragmentBinding.bind(view)
        setupBinding()
        locationHandler()

        if (savedInstanceState != null) {
            requestingLocationUpdates =
                savedInstanceState.getBoolean(AppConstants.Common.LOCATION_UPDATE_KEY)
        }

        networkHelperClass =
            (requireActivity().applicationContext as MyApplication).getNetworkHelper()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create()
        locationRequest.interval = 3000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        if (isLocationPermissionGranted()) {
            checkLocationSettings()
        } else {
            getLocationPermission()
        }



        userListAdapter = UserListAdapter(userListInterface)
        userSearchListAdapter = UserListAdapter(userListInterface)
        binding.recyclerViewUserList.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = userListAdapter.withLoadStateHeaderAndFooter(
                header = UserListLoadStateAdapter { userListAdapter.retry() },
                footer = UserListLoadStateAdapter { userListAdapter.retry() },
            )
        }

        binding.recyclerViewSearchUserList.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = userSearchListAdapter
        }


        userListAdapter.addLoadStateListener { loadState ->
            val refreshState = loadState.mediator?.refresh
            binding.recyclerViewUserList.isVisible = refreshState is LoadState.NotLoading
            binding.progressBar.isVisible = refreshState is LoadState.Loading
            binding.buttonRetry.isVisible = refreshState is LoadState.Error
            handleError(loadState)
        }

        binding.buttonRetry.setOnClickListener {
            userListAdapter.retry()
        }

        binding.weatherLayout.setOnClickListener {
            if (isLocationPermissionGranted()) {
                checkLocationSettings()
            } else {
                getLocationPermission()
            }
        }

        binding.imageButtonClose.setOnClickListener {
            binding.editTextSearch.text.clear()
            binding.editTextSearch.clearFocus()
            binding.root.hideSoftInput()
        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.imageButtonClose.visibility = View.VISIBLE
                } else {
                    binding.imageButtonClose.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {

                    binding.recyclerViewSearchUserList.isVisible = true
                    binding.recyclerViewUserList.isVisible = false
                    searchView(s.toString())

                } else {
                    binding.recyclerViewSearchUserList.isVisible = false
                    binding.recyclerViewUserList.isVisible = true
                    setupUserRecyclerViewDataSet()
                }
            }

        })
        if (requestingLocationUpdates) setupLocationUpdate()
        setupUserRecyclerViewDataSet()
    }

    private fun checkLocationSettings() {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()
        val client = LocationServices
            .getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(request)

        task.addOnSuccessListener {
            requestingLocationUpdates = true
            setupLocationUpdate()
        }
        task.addOnFailureListener {
            requestingLocationUpdates = false
            if (it is ResolvableApiException) {

                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    it.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationUpdate() {
        if (isLocationPermissionGranted()) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }


    private fun handleError(loadState: CombinedLoadStates) {
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
            ?: loadState.mediator?.refresh as? LoadState.Error

        errorState?.let {
            Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun locationHandler() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    checkLocationSettings()
                } else {
                    showLocationPermissionDenied()
                }
            }
    }

    private fun showLocationPermissionDenied() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.message))
            .setMessage(getString(R.string.enable_location_permission_step))
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }.show()
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocationPermission() {
        requestPermissionLauncher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }


    private fun searchView(searchString: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchUserList(searchString).collectLatest {
                userSearchListAdapter.submitData(it)
            }
        }
    }

    private fun setupUserRecyclerViewDataSet() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUserList().collectLatest {
                userListAdapter.submitData(it)
            }
        }
    }

    private fun setupBinding() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val appViewModelProvider = AppViewModelFactory(
            AppRepository.getInstance(appDatabase)
        )
        viewModel =
            ViewModelProvider(this, appViewModelProvider).get(UserListViewModel::class.java)
        _binding.apply {
            lifecycleOwner = this@UserList.viewLifecycleOwner
            executePendingBindings()
        }
    }

    private fun View.hideSoftInput() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun displayWeatherDetails(latLngLocation: Location) {
        lifecycleScope.launchWhenStarted {


            val location = String.format(
                "%s,%s",
                latLngLocation.latitude.toString(),
                latLngLocation.longitude.toString()
            )
            try {
                when (val weatherDetails = viewModel.getWeatherDetails(location = location)) {
                    is WeatherResponse.Success -> showWeatherDetails(weatherDetails.success)
                    is WeatherResponse.Failure -> showExceptionMessage(weatherDetails.failure)
                    is WeatherResponse.HttpErrorCode.Exception -> showHttpCodeErrorMessage(
                        weatherDetails.exception
                    )
                }
            } catch (e: IOException) {
                Log.i(TAG, "displayWeatherDetails: $e")
            } catch (e: UnknownHostException) {
                Log.i(TAG, "displayWeatherDetails: $e")
            }
        }
    }

    private fun showHttpCodeErrorMessage(exception: String) {
        Toast.makeText(requireContext(), exception, Toast.LENGTH_LONG).show()
    }

    private fun showExceptionMessage(exception: String) {
        Toast.makeText(requireContext(), exception, Toast.LENGTH_LONG).show()
    }

    private fun showWeatherDetails(weatherDetails: WeatherModel) {
        binding.textViewWeatherLocation.text =
            String.format(
                "%s %s %s",
                weatherDetails.current.temp_c,
                "\u00B0",
                weatherDetails.location.name
            )
        binding.textViewWeatherText.apply {
            isSelected = true
            text = weatherDetails.current.condition.text
        }
        Glide
            .with(requireContext())
            .load("https:${weatherDetails.current.condition.icon}")
            .into(binding.imageViewWeatherIcon)

        stopLocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(AppConstants.Common.LOCATION_UPDATE_KEY, requestingLocationUpdates)
    }


    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        requestingLocationUpdates = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}