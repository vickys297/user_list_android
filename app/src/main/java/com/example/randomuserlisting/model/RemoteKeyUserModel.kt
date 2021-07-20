package com.example.randomuserlisting.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.randomuserlisting.utils.AppConstants

@Entity(tableName = AppConstants.LocalDatabase.REMOTE_TABLE_NAME)
data class RemoteKeyUserModel(
    @PrimaryKey
    val phone: String,
    val previousKey: Int?,
    val nextKey: Int?
)
