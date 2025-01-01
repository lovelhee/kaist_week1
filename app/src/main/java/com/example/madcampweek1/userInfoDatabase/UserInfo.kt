package com.example.madcampweek1.userInfoDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserInfo(
    @PrimaryKey(autoGenerate = true) val userCode: Int = 0,
    val id: String,
    val password: String,
    val userType: String
)
