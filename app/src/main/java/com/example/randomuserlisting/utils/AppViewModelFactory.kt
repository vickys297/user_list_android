package com.example.randomuserlisting.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.example.randomuserlisting.ui.fragments.splashScreen.SplashScreenViewModel
import com.example.randomuserlisting.ui.fragments.userDetails.UserDetailsViewModel
import com.example.randomuserlisting.ui.fragments.userList.UserListViewModel

class AppViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    @ExperimentalPagingApi
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            SplashScreenViewModel::class.java -> {
                SplashScreenViewModel(appRepository) as T
            }
            UserListViewModel::class.java -> {
                UserListViewModel(appRepository) as T
            }
            UserDetailsViewModel::class.java->{
                UserDetailsViewModel(appRepository) as T
            }
            else -> {
                throw Exception("View model not found")
            }
        }
    }
}