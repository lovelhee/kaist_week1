package com.example.madcampweek1.userInfoDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.madcampweek1.GalleryDataBase.GalleryImage

@Dao
interface UserInfoDao {

    // 사용자 추가
    @Insert
    suspend fun insertUser(userInfo: UserInfo)

    // 모든 사용자 조회
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserInfo>

    // 아이디로 사용자 조회
    @Query("SELECT * FROM users WHERE id = :id AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(id: String, password: String): UserInfo?

    // 유저코드로 사용자 조회
    @Query("SELECT * FROM users WHERE userCode = :userCode")
    suspend fun getUserByUserCode(userCode: Int): UserInfo?

}