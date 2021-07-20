package com.example.randomuserlisting.utils

object AppConstants {

    object Navigation {
        const val USER_KEY = "user_item"
        const val LOCATION_LAT = "location_lat"
        const val LOCATION_LNG = "location_lng"
    }

    object LocalDatabase {
        const val DATABASE_NAME = "user.localDatabase"
        const val USER_TABLE_NAME = "user"
        const val REMOTE_TABLE_NAME = "remote_user"
    }

    object Network {
        const val USER_URL = "https://randomuser.me"
        const val WEATHER_URL = "https://api.weatherapi.com/"
        const val DEFAULT_LOAD_SIZE = "25"
        const val PAGE_SIZE = 25
    }

    object Key{
        const val WEATHER_API_KEY="2841b62f5ef743b4994154905211807"
    }

    object Common{
        const val LOCATION_UPDATE_KEY = "Location_Key"
    }

    object Preferences{
        const val PREFERENCE_KEY ="appPreferences"
        const val PREVIOUS_LOCATION_LAT ="previousLocationLat"
        const val PREVIOUS_LOCATION_LNG ="previousLocationLng"
    }
}