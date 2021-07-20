package com.example.randomuserlisting.model

data class UserResponseModel(
    val results: ArrayList<UserModel>,
    val info: Info
) {
    data class Info(
        val seed: String,
        val results: Int,
        val page: Int,
        val version: String
    )
}
