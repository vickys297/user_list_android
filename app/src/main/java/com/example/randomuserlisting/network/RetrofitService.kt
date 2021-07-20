package com.example.randomuserlisting.network

import com.example.randomuserlisting.BuildConfig
import com.example.randomuserlisting.utils.AppConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


class RetrofitServices {


    private var retrofit: Retrofit.Builder

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .connectTimeout(timeout = 30L, TimeUnit.SECONDS)
            .readTimeout(timeout = 30L, TimeUnit.SECONDS)
            .addNetworkInterceptor(ConnectivityInterceptor())


        if (BuildConfig.DEBUG) {
            client.addInterceptor(interceptor)
        }

        retrofit = Retrofit.Builder().apply {
            client(client.build())
        }

    }


    companion object {
        fun getInstance() = RetrofitServices()
    }

    fun <S> createUserListService(serviceClass: Class<S>): S {
        val builder = retrofit.apply {
            baseUrl(AppConstants.Network.USER_URL)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
        return builder.create(serviceClass)
    }

    fun <S> createWeatherService(serviceClass: Class<S>): S {
        val builder = retrofit.apply {
            baseUrl(AppConstants.Network.WEATHER_URL)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
        return builder.create(serviceClass)
    }
}