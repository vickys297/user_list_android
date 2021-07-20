package com.example.randomuserlisting.ui.fragments.userDetails

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.transition.Visibility
import com.example.randomuserlisting.model.UserModel
import com.example.randomuserlisting.model.WeatherModel
import com.example.randomuserlisting.model.WeatherResponse
import com.example.randomuserlisting.utils.AppRepository

class UserDetailsViewModel(private val appRepository: AppRepository) : ViewModel() {
    var userDetails = MutableLiveData<UserModel>()
    var weatherDetails = MutableLiveData<WeatherModel>()


    suspend fun getWeatherDetails(location: String): WeatherResponse {
        return appRepository.getWeatherReport(location)
    }

}