package com.example.madcampweek1.generalAppDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.madcampweek1.GalleryDataBase.GalleryImage
import com.example.madcampweek1.GalleryDataBase.GalleryImageDao
import com.example.madcampweek1.member.Converters
import com.example.madcampweek1.userInfoDatabase.UserInfo
import com.example.madcampweek1.userInfoDatabase.UserInfoDao

@Database(entities = [UserInfo::class, GalleryImage::class], version = 1)
@TypeConverters(Converters::class)
abstract class GeneralAppDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
    abstract fun galleryImageDao(): GalleryImageDao

    companion object {
        @Volatile
        private var instance: GeneralAppDatabase? = null

        fun getInstance(context: Context): GeneralAppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    GeneralAppDatabase::class.java,
                    "general_app_database"
                ).build().also { instance = it }
            }
    }
}