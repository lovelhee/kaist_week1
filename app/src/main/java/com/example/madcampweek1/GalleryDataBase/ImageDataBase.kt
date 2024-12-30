package com.example.madcampweek1.GalleryDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GalleryImage::class], version = 1)
abstract class ImageDataBase : RoomDatabase() {
    abstract fun galleryImageDao(): GalleryImageDao

    companion object {
        @Volatile
        private var INSTANCE: ImageDataBase? = null

        fun getDatabase(context: Context): ImageDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImageDataBase::class.java,
                    "gallery_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}