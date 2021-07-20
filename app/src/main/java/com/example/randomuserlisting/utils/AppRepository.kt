package com.example.randomuserlisting.utils

import androidx.paging.*
import com.example.randomuserlisting.dataSource.UserDataSourceMediator
import com.example.randomuserlisting.localDatabase.AppDatabase
import com.example.randomuserlisting.model.*
import com.example.randomuserlisting.network.NoConnectivityException
import com.example.randomuserlisting.network.RetrofitServices
import com.example.randomuserlisting.network.WeatherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.net.UnknownHostException

class AppRepository(val appDatabase: AppDatabase) {

    private val retrofitServices = RetrofitServices.getInstance()

    private val userDao = appDatabase.userDao()

    companion object {
        fun getInstance(appDatabase: AppDatabase) =
            AppRepository(appDatabase = appDatabase)
    }

    @ExperimentalPagingApi
    fun getMoreUserList(viewModelScope: CoroutineScope): Flow<PagingData<UserModel>> {
        val pagingSource = { appDatabase.userDao().getUserList() }
        return Pager(
            config = PagingConfig(
                pageSize = AppConstants.Network.PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = pagingSource,
            remoteMediator = UserDataSourceMediator(
                database = appDatabase,
                retrofitServices = retrofitServices
            )
        ).flow
            .cachedIn(viewModelScope)
    }

    suspend fun getWeatherReport(location: String): WeatherResponse {
        val request = retrofitServices.createWeatherService(WeatherService::class.java)
        val response = request.getCurrentCondition(
            key = AppConstants.Key.WEATHER_API_KEY,
            location = location,
            airQuality = true
        )


        return try {
            if (response.isSuccessful) {
                val data = response.body()
                if (data == null) {
                    WeatherResponse.Failure("No data found")
                } else {
                    WeatherResponse.Success(data)
                }
            } else {
                when (response.code()) {
                    400 -> WeatherResponse.HttpErrorCode.Exception("Bad Request")
                    403 -> WeatherResponse.HttpErrorCode.Exception("Access to resource is forbidden")
                    404 -> WeatherResponse.HttpErrorCode.Exception("Resource not found")
                    500 -> WeatherResponse.HttpErrorCode.Exception("Internal server error")
                    502 -> WeatherResponse.HttpErrorCode.Exception("Bad Gateway")
                    301 -> WeatherResponse.HttpErrorCode.Exception("Resource has been removed permanently")
                    302 -> WeatherResponse.HttpErrorCode.Exception("Resource moved, but has been found")
                    else -> WeatherResponse.HttpErrorCode.Exception("Something went wrong")
                }
            }
        } catch (e: Exception) {
            WeatherResponse.HttpErrorCode.Exception(e.message!!)
        } catch (e: IOException) {
            WeatherResponse.HttpErrorCode.Exception(e.message!!)
        }
    }


    fun searchUserList(
        searchString: String,
        viewModelScope: CoroutineScope
    ): Flow<PagingData<UserModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = AppConstants.Network.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { userDao.searchUserList("%$searchString%") }
        ).flow
            .cachedIn(viewModelScope)
    }


}