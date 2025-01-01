package com.example.madcampweek1.GalleryDataBase

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import com.example.madcampweek1.userInfoDatabase.UserInfo

@Entity(tableName = "gallery_images", foreignKeys = [
    ForeignKey(
        entity = UserInfo::class,
        parentColumns = ["userCode"],
        childColumns = ["userCode"],
        onDelete = ForeignKey.CASCADE
    )
])
data class GalleryImage(
    @PrimaryKey(autoGenerate = true) val imageId: Int = 0,
    @ColumnInfo(name = "image_uri") val imageUri: String,
    @ColumnInfo(name = "timestamp") val timestamp: String,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "userCode") val userCode: Int
)
