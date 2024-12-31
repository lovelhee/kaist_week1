package com.example.madcampweek1.help

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.madcampweek1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // MapFragment 초기화
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 예제 위치 설정 (부산 해운대구 좌표)
        val location = LatLng(35.1631, 129.1635)
        googleMap.addMarker(MarkerOptions().position(location).title("침착손해사정사무소"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}