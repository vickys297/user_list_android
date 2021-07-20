package com.example.randomuserlisting.utils

import android.content.Context
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat

class AppFunctions {

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }
}