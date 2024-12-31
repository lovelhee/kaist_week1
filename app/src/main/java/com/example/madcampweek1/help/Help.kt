package com.example.madcampweek1.help

import java.io.Serializable

data class Help(
    val name: String,
    val tag: String,
    val phoneNumber: String,
    val email: String,
    val office: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val content: String,
    val imageUrl: String,
    val talkRate: String,
    val talkCount: String
) : Serializable
