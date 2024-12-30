package com.example.madcampweek1.GalleryDataBase

import androidx.room.Insert
import androidx.room.Dao
import androidx.room.Query

@Dao
interface GalleryImageDao {
    @Insert
    suspend fun insert(image:GalleryImage)

    @Query("SELECT * FROM gallery_images")
    suspend fun getAllImages(): List<GalleryImage>

    @Query("DELETE FROM gallery_images WHERE image_uri = :uri")
    suspend fun deleteByUri(uri: String)
}