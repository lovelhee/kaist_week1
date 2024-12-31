package com.example.madcampweek1.UserDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userCode: Int = 0,
    val id: String,
    val password: String,
    val userType: String
)
