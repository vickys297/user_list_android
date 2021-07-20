package com.example.randomuserlisting.localDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.randomuserlisting.model.RemoteKeyUserModel
import com.example.randomuserlisting.model.UserModel
import com.example.randomuserlisting.utils.AppConstants

@Database(
    entities = [UserModel::class, RemoteKeyUserModel::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDAO
    abstract fun remoteUserDao(): RemoteKeyUserDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(AppDatabase::class) {
                instance ?: database(context).also { instance = it }
            }
        }

        private fun database(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                AppConstants.LocalDatabase.DATABASE_NAME
            ).build()
        }
    }
}