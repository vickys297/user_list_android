package com.example.randomuserlisting.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class ConnectivityInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (NetworkHelperClass.instance.hasNetworkConnection()) {
            return chain.proceed(chain.request())
        } else {
            throw NoConnectivityException()
        }
    }
}


class NetworkHelperClass {
    private lateinit var connectivityManager: ConnectivityManager

    fun createService(context: Context) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun hasNetworkConnection(): Boolean {
        var result = false // Returns connection type. 0: none; 1: mobile data; 2: wifi
        connectivityManager.run {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        result = true
                    }
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        result = true
                    }
                    hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                        result = true
                    }
                }
            }
        }

        return result
    }

    companion object {
        val instance = NetworkHelperClass()
    }
}

class NoConnectivityException : IOException() {
    // You can send any message whatever you want from here.
    override val message: String
        get() = "Connection error, check your network"
    // You can send any message whatever you want from here.
}
