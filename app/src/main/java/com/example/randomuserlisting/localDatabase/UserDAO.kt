package com.example.randomuserlisting.localDatabase

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.randomuserlisting.model.UserModel

@Dao
interface UserDAO {

    @Query("Select * From user")
    fun getUserList(): PagingSource<Int, UserModel>

    @Query("Select COUNT(*) From user")
    fun hasData(): Int

    @Query("DELETE from user")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userData: ArrayList<UserModel>)

    @Query("SELECT * FROM user WHERE firstName LIKE :searchString OR lastName LIKE :searchString ")
    fun searchUserList(searchString: String): PagingSource<Int, UserModel>
}