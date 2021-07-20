package com.example.randomuserlisting.localDatabase

import androidx.room.*
import com.example.randomuserlisting.model.RemoteKeyUserModel

@Dao
interface RemoteKeyUserDao {

    @Query("Select * From remote_user WHERE phone = :phone")
    suspend fun getKeyById(phone: String): RemoteKeyUserModel

    @Query("Delete from remote_user")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userData: List<RemoteKeyUserModel>)

}