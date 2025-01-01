package com.example.madcampweek1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.madcampweek1.loginJh.LoginJhActivity
import com.example.madcampweek1.member.MemberActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 애니메이션
        val splashImage = findViewById<ImageView>(R.id.splash_image)
        val scaleUpAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        splashImage.startAnimation(scaleUpAnimation)

        // 로그인 화면으로
        scaleUpAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                val intent = Intent(this@SplashActivity, LoginJhActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
    }
}