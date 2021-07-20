package com.example.randomuserlisting.model

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class TypeConverterHelper {

    private val gson = Gson()

    @TypeConverter
    fun fromLocation(location: UserModel.Location): String {
        return gson.toJson(location)
    }

    @TypeConverter
    fun toLocation(string: String): UserModel.Location {
        return gson.fromJson(string, object : TypeToken<UserModel.Location?>() {}.type)
    }


    @TypeConverter
    fun fromDOB(location: UserModel.DOB): String {
        return gson.toJson(location)
    }

    @TypeConverter
    fun toDOB(string: String): UserModel.DOB {
        return gson.fromJson(string, object : TypeToken<UserModel.DOB?>() {}.type)
    }


    @TypeConverter
    fun fromProfilePictures(location: UserModel.ProfilePictures): String {
        return gson.toJson(location)
    }

    @TypeConverter
    fun toProfilePictures(string: String): UserModel.ProfilePictures {
        return gson.fromJson(string, object : TypeToken<UserModel.ProfilePictures?>() {}.type)
    }


}
