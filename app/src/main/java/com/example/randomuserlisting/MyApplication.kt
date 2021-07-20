package com.example.randomuserlisting

import android.app.Application
import com.example.randomuserlisting.network.NetworkHelperClass

class MyApplication : Application() {

    lateinit var networkHelperClass: NetworkHelperClass

    override fun onCreate() {
        super.onCreate()

        networkHelperClass = NetworkHelperClass.instance
        networkHelperClass.createService(this@MyApplication)
    }

    fun getNetworkHelper(): NetworkHelperClass {
        return networkHelperClass
    }
}