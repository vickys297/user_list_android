package com.example.randomuserlisting.utils

import android.content.Context

class AppPreferences {

    fun savePreferences(key: String, value: String, context: Context) {
        val preferences = context.getSharedPreferences(
            AppConstants.Preferences.PREFERENCE_KEY,
            Context.MODE_PRIVATE
        ).edit()
        preferences.putString(key, value)
        preferences.apply()
    }


    fun getPreferences(key: String, default: String, context: Context): String {
        val preferences = context.getSharedPreferences(
            AppConstants.Preferences.PREFERENCE_KEY,
            Context.MODE_PRIVATE
        )
       return preferences.getString(key, default)!!
    }
}