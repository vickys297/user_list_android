package com.example.randomuserlisting.network

import com.example.randomuserlisting.model.WeatherModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("v1/current.json")
    suspend fun getCurrentCondition(
        @Query("key")
        key: String,

        @Query("q")
        location: String,

        @Query("aqi")
        airQuality: Boolean
    ): Response<WeatherModel>
}