package com.example.randomuserlisting.ui.fragments.userDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.randomuserlisting.R
import com.example.randomuserlisting.databinding.UserDetailsFragmentBinding
import com.example.randomuserlisting.localDatabase.AppDatabase
import com.example.randomuserlisting.model.UserModel
import com.example.randomuserlisting.model.WeatherModel
import com.example.randomuserlisting.model.WeatherResponse
import com.example.randomuserlisting.utils.AppConstants
import com.example.randomuserlisting.utils.AppRepository
import com.example.randomuserlisting.utils.AppViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.*

internal val TAG = UserDetails::class.java.canonicalName

class UserDetails : Fragment(R.layout.user_details_fragment) {


    private lateinit var weatherDataObserver: Observer<in WeatherModel>
    private lateinit var userDataObserver: Observer<in UserModel>
    private lateinit var _binding: UserDetailsFragmentBinding
    private val binding get() = _binding
    private lateinit var viewModel: UserDetailsViewModel
    private lateinit var userDetails: UserModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = UserDetailsFragmentBinding.bind(view)
        setupBinding()
        setupObserver()


        if (savedInstanceState != null) {
            val data = Gson().fromJson(
                savedInstanceState.getString(AppConstants.Navigation.USER_KEY),
                UserModel::class.java
            )
            userDetails = data
        } else {
            arguments?.let {
                val data = Gson().fromJson(
                    it.getString(AppConstants.Navigation.USER_KEY),
                    UserModel::class.java
                )
                userDetails = data
            }
        }


        binding.imageButtonEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(userDetails.email))
            requireContext().startActivity(intent)
        }

        binding.imageButtonMap.setOnClickListener {
            val uri: String =
                java.lang.String.format(
                    Locale.ENGLISH,
                    "geo:%s,%s",
                    userDetails.location.coordinates.latitude,
                    userDetails.location.coordinates.longitude
                )
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            requireContext().startActivity(intent)
        }


        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.userDetails.postValue(userDetails)
    }


    private fun setupObserver() {

        userDataObserver = Observer {
            it?.let {
                Glide
                    .with(requireContext())
                    .load(it.picture.large)
                    .placeholder(getProgressBar())
                    .fallback(R.drawable.image_placeholder)
                    .centerCrop()
                    .into(binding.appBarImage)

                getWeatherDetails(it.location.coordinates)
            }
        }

        weatherDataObserver = Observer {

            binding.linearLayoutWeather.isVisible = it != null
            binding.linearLayoutMiscellaneous.isVisible = it != null
            binding.linearLayoutWeatherError.isVisible = it == null
            binding.progressBar.isVisible = false

            it?.let {

                Log.i(TAG, "setupObserver: https:${it.current.condition.icon}")
                Glide
                    .with(requireContext())
                    .load("https:${it.current.condition.icon}")
                    .placeholder(getProgressBar())
                    .fallback(R.drawable.image_placeholder)
                    .centerCrop()
                    .into(binding.imageViewWeatherIcon)

                binding.layoutAirQuality.isVisible = it.current.air_quality != null
            }
        }


        viewModel.userDetails.observe(viewLifecycleOwner, userDataObserver)
        viewModel.weatherDetails.observe(viewLifecycleOwner, weatherDataObserver)
    }

    private fun getWeatherDetails(coordinates: UserModel.Location.Coordinates) {
        viewLifecycleOwner.lifecycleScope.launch {


            val response = viewModel.getWeatherDetails(
                String.format(
                    "%s,%s",
                    coordinates.latitude, coordinates.longitude
                )
            )

            when (response) {
                is WeatherResponse.Success -> {
                    viewModel.weatherDetails.postValue(response.success)
                }
                is WeatherResponse.Failure -> {
                    showWeatherDetailsFailureMessage()
                }
                is WeatherResponse.HttpErrorCode.Exception -> {
                    showWeatherDetailsFailureMessage()
                }
            }

        }
    }

    private fun showWeatherDetailsFailureMessage() {
        Toast.makeText(requireContext(), "unable to get weather details", Toast.LENGTH_SHORT).show()


        binding.linearLayoutWeatherError.isVisible = true

        binding.linearLayoutWeather.isVisible = false
        binding.linearLayoutMiscellaneous.isVisible = false
        binding.progressBar.isVisible = false
    }


    private fun setupBinding() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val appViewModelProvider = AppViewModelFactory(
            AppRepository.getInstance(appDatabase)
        )
        viewModel =
            ViewModelProvider(this, appViewModelProvider).get(UserDetailsViewModel::class.java)
        binding.apply {
            viewModel = this@UserDetails.viewModel
            lifecycleOwner = this@UserDetails.viewLifecycleOwner
            executePendingBindings()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(AppConstants.Navigation.USER_KEY, Gson().toJson(userDetails))
    }

    private fun getProgressBar(): CircularProgressDrawable {
        return CircularProgressDrawable(binding.root.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }

    }

}