package com.example.madcampweek1.member

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madcampweek1.MainActivity
import com.example.madcampweek1.R
import com.example.madcampweek1.databinding.ActivityMemberBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class MemberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemberBinding
    private var isNameValid = false
    private var isVehicleSelected = false
    private var isBrandSelected = false
    private var isInsuranceSelected = false
    private var isEmergencyContactValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etName = findViewById<EditText>(R.id.etName)
        val btnComplete = findViewById<Button>(R.id.btnComplete)

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isNameValid = !s.isNullOrEmpty()
                validateInput()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        for (i in 0 until binding.glVehicleType.childCount) {
            val child = binding.glVehicleType.getChildAt(i)
            if (child is RadioButton) {
                child.setOnClickListener {
                    for (j in 0 until binding.glVehicleType.childCount) {
                        val sibling = binding.glVehicleType.getChildAt(j)
                        if (sibling is RadioButton && sibling != child) {
                            sibling.isChecked = false
                        }
                    }
                    isVehicleSelected = true
                    validateInput()
                }
            }
        }

        for (i in 0 until binding.glChipGroup.childCount) {
            val child = binding.glChipGroup.getChildAt(i)
            if (child is Chip) {
                child.setOnClickListener {
                    for (j in 0 until binding.glChipGroup.childCount) {
                        val sibling = binding.glChipGroup.getChildAt(j)
                        if (sibling is Chip && sibling != child) {
                            sibling.isChecked = false
                        }
                    }
                    isBrandSelected = isAnyChipSelected()
                    validateInput()
                }
            }
        }

        for (i in 0 until binding.glInsurance.childCount) {
            val child = binding.glInsurance.getChildAt(i)
            if (child is CheckBox) {
                child.setOnCheckedChangeListener { _, _ ->
                    isInsuranceSelected = checkInsuranceSelected()
                    validateInput()
                }
            }
        }

        binding.etEmergencyContact1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isEmergencyContactValid = !s.isNullOrEmpty()
                validateInput()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnComplete.setOnClickListener {
            saveDataToDatabase()
            val selectedInsurances = getSelectedInsurances()
            val selectedVehicleBrand = getSelectedChipText()
            val emergencyContacts = getEmergencyContacts()

            val intent = Intent(this, MakeActivity::class.java)
            intent.putStringArrayListExtra("selectedInsurances", ArrayList(selectedInsurances))
            intent.putExtra("selectedVehicleBrand", selectedVehicleBrand)
            intent.putStringArrayListExtra("emergencyContacts", ArrayList(emergencyContacts))
            intent.putExtra("USER_NAME", binding.etName.text.toString())
            startActivity(intent)
            finish()
        }

    }

    private fun validateInput() {
        val isAllValid = isNameValid && isVehicleSelected && isBrandSelected && isInsuranceSelected && isEmergencyContactValid
        binding.btnComplete.isEnabled = isAllValid
        if (isAllValid) {
            binding.btnComplete.setBackgroundColor(Color.parseColor("#1D3574"))
            binding.btnComplete.text = "침착맨 정보 입력 완료 !"
        } else {
            binding.btnComplete.setBackgroundColor(Color.parseColor("#A5A5A5"))
            binding.btnComplete.text = "침착맨! 정보를 입력해 주세요."
        }
    }

    private fun checkInsuranceSelected(): Boolean {
        for (i in 0 until binding.glInsurance.childCount) {
            val child = binding.glInsurance.getChildAt(i)
            if (child is CheckBox && child.isChecked) {
                return true
            }
        }
        return false
    }

    private fun getSelectedVehicleType(): String {
        for (i in 0 until binding.glVehicleType.childCount) {
            val child = binding.glVehicleType.getChildAt(i)
            if (child is RadioButton && child.isChecked) {
                return child.text.toString()
            }
        }
        return ""
    }

    private fun isAnyChipSelected(): Boolean {
        for (i in 0 until binding.glChipGroup.childCount) {
            val child = binding.glChipGroup.getChildAt(i)
            if (child is com.google.android.material.chip.Chip && child.isChecked) {
                return true
            }
        }
        return false
    }

    private fun saveDataToDatabase() {
        val user = User(
            name = binding.etName.text.toString(),
            vehicleType = getSelectedVehicleType(),
            vehicleBrand = getSelectedChipText(),
            insurances = getSelectedInsurances(),
            emergencyContacts = getEmergencyContacts()
        )
        Thread {
            AppDatabase.getInstance(this).userDao().insertUser(user)
        }.start()
    }

    private fun getSelectedChipText(): String {
        for (i in 0 until binding.glChipGroup.childCount) {
            val child = binding.glChipGroup.getChildAt(i)
            if (child is com.google.android.material.chip.Chip && child.isChecked) {
                return child.text.toString()
            }
        }
        return ""
    }

    private fun getSelectedInsurances(): List<String> {
        val selectedInsurances = mutableListOf<String>()
        for (i in 0 until binding.glInsurance.childCount) {
            val child = binding.glInsurance.getChildAt(i)
            if (child is CheckBox && child.isChecked) {
                selectedInsurances.add(child.text.toString())
            }
        }
        return selectedInsurances
    }

    private fun getEmergencyContacts(): List<String> {
        return listOfNotNull(
            binding.etEmergencyContact1.text.toString().takeIf { it.isNotEmpty() },
            binding.etEmergencyContact2.text.toString().takeIf { it.isNotEmpty() },
            binding.etEmergencyContact3.text.toString().takeIf { it.isNotEmpty() }
        )
    }
}
