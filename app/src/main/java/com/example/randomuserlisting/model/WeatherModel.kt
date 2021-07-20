package com.example.randomuserlisting.model

data class WeatherModel(
    val location: WeatherLocationModel,
    val current: WeatherCurrentModel
) {
    data class WeatherLocationModel(
        val name: String,
        val region: String,
        val country: String,
        val lat: String,
        val lon: String,
        val tz_id: String,
        val localtime: String
    )

    data class WeatherCurrentModel(
        val last_updated: String,
        val temp_c: String,
        val temp_f: String,
        val is_day: Int,
        val condition: WeatherCondition,
        val wind_mph: String,
        val wind_kph: String,
        val wind_degree: String,
        val wind_dir: String,
        val pressure_mb: String,
        val pressure_in: String,
        val precip_mm: String,
        val precip_in: String,
        val humidity: String,
        val cloud: String,
        val feelslike_c: String,
        val feelslike_f: String,
        val vis_km: String,
        val vis_miles: String,
        val uv: String,
        val gust_mph: String,
        val gust_kph: String,
        val air_quality: WeatherAirQualityModel?
    ) {
        data class WeatherCondition(
            val text: String,
            val icon: String
        )

        data class WeatherAirQualityModel(
            val co: String,
            val no2: String,
            val o3: String,
            val so2: String,
            val pm2_5: String,
            val pm10: String
        )
    }
}
