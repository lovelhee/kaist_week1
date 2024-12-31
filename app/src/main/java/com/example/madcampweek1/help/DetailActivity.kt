package com.example.madcampweek1.help

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.madcampweek1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var help: Help

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        help = intent.getSerializableExtra("help") as Help

        setupUI()

        setupButtons()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupButtons() {

        findViewById<ImageButton>(R.id.imgBtnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnEmail).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // 이메일 전용 필터
                putExtra(Intent.EXTRA_EMAIL, arrayOf(help.email)) // 수신자 이메일
                putExtra(Intent.EXTRA_SUBJECT, "상담 요청") // 제목
                putExtra(Intent.EXTRA_TEXT, "안녕하세요, 상담을 요청드립니다.") // 내용
            }
            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(emailIntent)
            }
        }

        findViewById<Button>(R.id.btnCall).setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${help.phoneNumber}") // 전화번호
            }
            if (callIntent.resolveActivity(packageManager) != null) {
                startActivity(callIntent)
            }
        }
    }

    private fun setupUI() {

        findViewById<ImageView>(R.id.ivProfile).let { imageView ->
            Glide.with(this)
                .load(help.imageUrl)
                .placeholder(R.drawable.default_contact_image)
                .into(imageView)
        }

        findViewById<TextView>(R.id.tvTag).text = "#${help.tag}"
        findViewById<TextView>(R.id.tvName).text = help.name
        findViewById<TextView>(R.id.tvOffice).text = help.office
        findViewById<TextView>(R.id.tvAddress).text = help.address
        findViewById<TextView>(R.id.tvReplyPercent).text = help.talkRate
        findViewById<TextView>(R.id.tvTalkPercent).text = help.talkCount
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val location = LatLng(help.latitude, help.longitude)
        googleMap.addMarker(MarkerOptions().position(location).title(help.office))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}