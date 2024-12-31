package com.example.madcampweek1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.madcampweek1.loginJh.LoginJhActivity
import com.example.madcampweek1.member.MemberActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginJhActivity::class.java) // 로그인 화면으로 넘어가게 수정 (2024.12.31 13:58)
            startActivity(intent)
            finish()
        }, 2500)
    }
}