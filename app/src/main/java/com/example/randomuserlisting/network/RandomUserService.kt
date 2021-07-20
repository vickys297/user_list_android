package com.example.randomuserlisting.network

import com.example.randomuserlisting.model.UserModel
import com.example.randomuserlisting.model.UserResponseModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RandomUserService {

    @GET("api/")
    suspend fun getUserList(@Query("results") result: String): Response<UserResponseModel>
}