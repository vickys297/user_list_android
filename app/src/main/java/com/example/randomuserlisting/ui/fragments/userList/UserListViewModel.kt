package com.example.randomuserlisting.ui.fragments.userList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.example.randomuserlisting.model.UserModel
import com.example.randomuserlisting.model.WeatherResponse
import com.example.randomuserlisting.utils.AppRepository
import kotlinx.coroutines.flow.Flow

@ExperimentalPagingApi
class UserListViewModel(private val appRepository: AppRepository) : ViewModel() {

    var userListRemoteDataSet = appRepository.getMoreUserList(viewModelScope)

    fun getUserList(): Flow<PagingData<UserModel>> {
        return userListRemoteDataSet
    }

    fun searchUserList(searchString: String): Flow<PagingData<UserModel>> {
        return appRepository.searchUserList(searchString, viewModelScope)
    }

    suspend fun getWeatherDetails(location: String): WeatherResponse {
        return appRepository.getWeatherReport(location)
    }
}