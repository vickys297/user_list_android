package com.example.randomuserlisting.ui.fragments.splashScreen

import android.Manifest
import android.animation.Animator
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.randomuserlisting.R
import com.example.randomuserlisting.databinding.FragmentSplashScreenBinding
import com.example.randomuserlisting.localDatabase.AppDatabase
import com.example.randomuserlisting.utils.AppRepository
import com.example.randomuserlisting.utils.AppViewModelFactory

class SplashScreen : Fragment(R.layout.fragment_splash_screen) {


    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var _binding: FragmentSplashScreenBinding
    private val binding: FragmentSplashScreenBinding get() = _binding

    private lateinit var viewModel: SplashScreenViewModel



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        _binding = FragmentSplashScreenBinding.bind(view)
        setupBinding()


        requestPermissionLauncher =
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (!isGranted) {
                    showLocationPermissionDeniedMessage()
                }
                findNavController().navigate(R.id.action_splashScreen_to_userList)
            }



        binding.lottieLayerName.addAnimatorListener(object:Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                validatePermissions()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

        })



    }

    private fun setupBinding() {
        viewModel = ViewModelProvider(
            this, AppViewModelFactory(
                AppRepository.getInstance(
                    AppDatabase.getInstance(requireContext())
                )
            )
        ).get(SplashScreenViewModel::class.java)
        binding.apply {
            lifecycleOwner = this@SplashScreen.viewLifecycleOwner
            executePendingBindings()
        }
    }


    private fun validatePermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                findNavController().navigate(R.id.action_splashScreen_to_userList)
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }


    private fun showLocationPermissionDeniedMessage() {
        Toast.makeText(requireContext(), getString(R.string.location_denied_note), Toast.LENGTH_SHORT).show()
    }

}