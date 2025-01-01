package com.example.madcampweek1.member

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madcampweek1.MainActivity
import com.example.madcampweek1.R

class MakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make)

        val tvName = findViewById<TextView>(R.id.tvName)
        val ivCarLogo = findViewById<ImageView>(R.id.ivCarlogo)

        val ivCar = findViewById<ImageView>(R.id.ivCar)
        val layoutText = findViewById<LinearLayout>(R.id.layoutText)

        val moveAnimation = AnimationUtils.loadAnimation(this, R.anim.move_to_car_center)
        ivCar.startAnimation(moveAnimation)
        ivCarLogo.startAnimation(moveAnimation)

        // 투명도 애니메이션 적용
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_text)
        layoutText.startAnimation(fadeInAnimation)

        // 전달받은 데이터 처리
        val userName = intent.getStringExtra("USER_NAME") ?: "이름"
        val carBrand = intent.getStringExtra("selectedVehicleBrand") ?: "기아"
        val selectedInsurances = intent.getStringArrayListExtra("selectedInsurances") ?: arrayListOf()
        val emergencyContacts = intent.getStringArrayListExtra("emergencyContacts") ?: arrayListOf()

        // 화면 구성
        tvName.text = "$userName"

        // 자동차 로고 변경
        val logoRes = when (carBrand) {
            "기아" -> R.drawable.ic_carkia
            "르노" -> R.drawable.ic_carleno
            "쉐보레" -> R.drawable.ic_carche
            "현대" -> R.drawable.ic_carhyun
            "벤츠" -> R.drawable.ic_benz
            "BMW" -> R.drawable.ic_carbmw
            "아우디" -> R.drawable.ic_caraudi
            "렉서스" -> R.drawable.ic_carlexus
            else -> R.drawable.ic_carimage
        }
        ivCarLogo.setImageResource(logoRes)

        // 5초 후 MainActivity로 데이터 전달
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java).apply {
                putStringArrayListExtra("selectedInsurances", selectedInsurances)
                putExtra("selectedVehicleBrand", carBrand)
                putStringArrayListExtra("emergencyContacts", emergencyContacts)
                putExtra("USER_NAME", userName)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}