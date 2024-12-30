package com.example.madcampweek1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.madcampweek1.contactTab.ContactsFragment
import com.example.madcampweek1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedInsurances: ArrayList<String>? = null
    private var selectedVehicleBrand: String? = null
    private var emergencyContacts: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedInsurances = intent.getStringArrayListExtra("selectedInsurances") ?: arrayListOf()
        selectedVehicleBrand = intent.getStringExtra("selectedVehicleBrand")
        emergencyContacts = intent.getStringArrayListExtra("emergencyContacts") ?: arrayListOf()

        replaceFragment(ContactsFragment(), createContactsBundle())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_contacts -> {
                    replaceFragment(ContactsFragment(), createContactsBundle())
                    true
                }
                R.id.navigation_gallery -> {
                    replaceFragment(GalleryFragment())
                    true
                }
                R.id.navigation_help -> {
                    replaceFragment(HelpFragment())
                    true
                }
                else -> false
            }
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun createContactsBundle(): Bundle {
        return Bundle().apply {
            putStringArrayList("selectedInsurances", selectedInsurances) // 리스트 전달
            putString("selectedVehicleBrand", selectedVehicleBrand)
            putStringArrayList("emergencyContacts", emergencyContacts)
        }
    }

    private fun replaceFragment(fragment: Fragment, bundle: Bundle? = null) {
        if (bundle != null) {
            fragment.arguments = bundle
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}