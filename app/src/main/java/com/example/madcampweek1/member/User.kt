package com.example.madcampweek1.member

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val vehicleType: String,
    val vehicleBrand: String,
    val insurances: List<String>,
    val emergencyContacts: List<String>
)
