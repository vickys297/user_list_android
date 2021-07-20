package com.example.randomuserlisting.model

import androidx.room.*
import com.example.randomuserlisting.utils.AppConstants
import java.io.Serializable
import java.lang.Exception
import java.lang.reflect.Executable
import java.text.SimpleDateFormat
import java.util.*


@Entity(
    tableName = AppConstants.LocalDatabase.USER_TABLE_NAME,
    indices = [
        Index(value = ["firstName"], unique = false),
        Index(value = ["lastName"], unique = false),
        Index(value = ["email"], unique = true),
        Index(value = ["cell"], unique = true),
        Index(value = ["nat"], unique = false)
    ]
)
data class UserModel(
    @Embedded
    val login: LoginModel,

    @Embedded
    val name: Name,
    val gender: String,

    @Embedded
    val location: Location,

    val email: String,

    @PrimaryKey
    val phone: String,

    val cell: String,

    @Embedded
    val picture: ProfilePictures,
    val nat: String,

    @Embedded(
        prefix = "dob_"
    )
    val dob: DOB,

    @Embedded(
        prefix = "registered_"
    )
    val registered: DOB
) : Serializable {

    data class LoginModel(
        val uuid: String,
        val username: String,
        val password: String,
        val salt: String,
        val md5: String,
        val sha1: String,
        val sha256: String,
    )

    data class Name(
        val title: String,

        @ColumnInfo(name = "firstName")
        val first: String,

        @ColumnInfo(name = "lastName")
        val last: String
    ) {
        fun getDisplayName(): String {
            return String.format("%s %s %s", title, first, last)
        }
    }

    data class Location(
        @Embedded
        val street: Street,
        val city: String,
        val state: String,
        val country: String,
        val postcode: String,

        @Embedded
        val coordinates: Coordinates,

        @Embedded
        val timezone: TimeZone,
    ) {
        data class Street(
            val number: Int,
            val name: String
        ){
            fun getDisplaySteer(): String {
                return String.format("%d, %s ", number, name)
            }
        }

        data class Coordinates(
            val latitude: String,
            val longitude: String
        )


        data class TimeZone(
            val offset: String,
            val description: String
        )

        fun getDisplayCity():String{
            return String.format("%s, Zip Code %s", city, postcode)
        }

        fun getDisplayState():String{
            return String.format("%s, %s", state, country)
        }
    }

    data class ProfilePictures(
        val large: String,
        val medium: String,
        val thumbnail: String
    )

    data class DOB(
        val date: String,
        val age: Int
    ) {
        fun getDisplayDOB(): String {
            return try {
                val calendar = Calendar.getInstance()
                calendar.time = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).parse(date)!!
                String.format(
                    "%s %s %s",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.YEAR)
                )
            } catch (e: Exception) {
                e.message.toString()
            }
        }

        fun getDisplayAge(): String {
            return age.toString()
        }
    }
}

