package com.example.madcampweek1.member

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User)
}
