package com.example.randomuserlisting.model

sealed class RandomUserListResponse {
    data class Success(val success: UserResponseModel) : RandomUserListResponse()
    data class Failure(val failure: String) : RandomUserListResponse()

    sealed class HttpErrorCode : RandomUserListResponse() {
        data class Exception(val exception: String) : HttpErrorCode()
    }
}

sealed class WeatherResponse {
    data class Success(val success: WeatherModel) : WeatherResponse()
    data class Failure(val failure: String) : WeatherResponse()

    sealed class HttpErrorCode : WeatherResponse() {
        data class Exception(val exception: String) : WeatherResponse()
    }
}