package com.example.madcampweek1.UserDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    // 사용자 추가
    @Insert
    suspend fun insertUser(user: User)

    // 모든 사용자 조회
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    // 아이디로 사용자 조회
    @Query("SELECT * FROM users WHERE id = :id AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(id: String, password: String): User?

}