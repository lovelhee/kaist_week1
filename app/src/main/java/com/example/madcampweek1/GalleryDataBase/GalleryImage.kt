package com.example.madcampweek1.GalleryDataBase

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "gallery_images")
data class GalleryImage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "image_uri") val imageUri: String,
    @ColumnInfo(name = "timestamp") val timestamp: String,
    @ColumnInfo(name = "position") val position: Int
)
