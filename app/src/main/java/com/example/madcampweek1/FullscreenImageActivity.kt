package com.example.madcampweek1

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.madcampweek1.databinding.ActivityFullscreenImageBinding

class FullscreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullscreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uriString = intent.getStringExtra("imageUri")
        uriString?.let {
            val uri = Uri.parse(it)
            binding.fullscreenImageView.setImageURI(uri)
        }
    }
}