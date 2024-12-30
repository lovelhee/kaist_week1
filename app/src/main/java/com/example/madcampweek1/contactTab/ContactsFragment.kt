package com.example.madcampweek1.contactTab

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcampweek1.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class ContactsFragment : Fragment() {

    private val contactList = mutableListOf<Contact>()
    private lateinit var adapter: ContactsAdapter
    private var selectedImageUri: Uri? = null
    private var btnSelectImageDialog: ImageButton? = null
    private val categoryOrder = listOf("보험사", "도로 관리", "견인 서비스", "긴급 출동")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvContacts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val selectedInsurances = arguments?.getStringArrayList("selectedInsurances") ?: arrayListOf()
        val selectedVehicleBrand = arguments?.getString("selectedVehicleBrand") ?: ""
        val emergencyContacts = arguments?.getStringArrayList("emergencyContacts") ?: arrayListOf()

        val filteredContacts = loadContactsFromJson().filter { contact ->
            when (contact.category) {
                "보험사" -> contact.name in selectedInsurances
                "긴급 출동" -> contact.name == selectedVehicleBrand // 선택된 차량 브랜드만 표시
                "도로 관리", "견인 서비스" -> true // 전체 표시
                else -> false // 기타 카테고리는 제외
            }
        }

        val emergencyContactObjects = emergencyContacts.map { phoneNumber ->
            Contact(
                name = "긴급 연락처",
                phoneNumber = phoneNumber,
                category = "긴급 연락처",
                imageUrl = "https://i.imgur.com/2pmUJOo.png" // 긴급 연락처는 이미지 없음
            )
        }

        contactList.clear()
        contactList.addAll(filteredContacts + emergencyContactObjects)
        contactList.sortWith(compareBy { categoryOrder.indexOf(it.category) })
        adapter = ContactsAdapter(contactList) { contact ->
            showCallDialog(contact)
        }
        recyclerView.adapter = adapter

        val imgBtnPlus = view.findViewById<ImageButton>(R.id.imgBtnPlus)
        imgBtnPlus.setOnClickListener { showAddContactDialog() }

        return view
    }

    private fun showCallDialog(contact: Contact) {
        AlertDialog.Builder(requireContext())
            .setTitle("전화를 거시겠습니까?")
            .setMessage("${contact.name}에게 전화를 거시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                makePhoneCall(contact.phoneNumber)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "전화 권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "전화 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_contact, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etPhoneNumber = dialogView.findViewById<EditText>(R.id.etPhoneNumber)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupCategory)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancle)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        btnSelectImageDialog = dialogView.findViewById(R.id.btnSelectImage)

        btnSelectImageDialog?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val selectedRadioId = radioGroup.checkedRadioButtonId
            val category = when (selectedRadioId) {
                R.id.radioInsurance -> "보험사"
                R.id.radioRoadManagement -> "도로 관리"
                R.id.radioTowing -> "견인 서비스"
                R.id.radioRepair -> "긴급 출동"
                else -> ""
            }

            if (name.isEmpty() || phoneNumber.isEmpty() || selectedImageUri == null || category == "") {
                Toast.makeText(requireContext(), "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contact = Contact(
                name = name,
                phoneNumber = phoneNumber,
                category = category,
                imageUrl = selectedImageUri.toString()
            )
            addContactToRecyclerView(contact)
            dialog.dismiss()


        }

        dialog.show()

    }

    private fun addContactToRecyclerView(contact: Contact) {
        contactList.add(contact)
        contactList.sortWith(compareBy { categoryOrder.indexOf(it.category) })
        adapter.notifyItemInserted(contactList.size - 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                selectedImageUri = imageUri
                Glide.with(requireContext())
                    .load(selectedImageUri ?: R.drawable.default_contact_image)
                    .circleCrop()
                    .into(btnSelectImageDialog!!)
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
        private const val REQUEST_CALL_PERMISSION = 200
    }

    private fun loadContactsFromJson(): List<Contact> {
        val inputStream = resources.openRawResource(R.raw.contacts)
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Contact>>() {}.type
        return Gson().fromJson(reader, type)
    }

}