package com.example.madcampweek1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcampweek1.RecyclerViewAdapter
import com.example.madcampweek1.RecyclerViewItem
import com.example.madcampweek1.databinding.FragmentGalleryBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.*
import androidx.activity.result.ActivityResultCallback
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView


class GalleryFragment : Fragment(R.layout.fragment_gallery) {
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var recyclerViewAdapters: MutableList<RecyclerViewAdapter>
    private lateinit var getImageLaunchers: MutableList<ActivityResultLauncher<Intent>>
    private lateinit var cameraLaunchers: MutableList<ActivityResultLauncher<Intent>>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var currentPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 프래그먼트의 레이아웃 설정
        binding = FragmentGalleryBinding.inflate(inflater, container, false)

        // 권한 요청 런처 초기화
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    showPermissionDeniedMessage()
                }
            }

        // 권한 확인 및 요청
        checkAndRequestPermission()

        // ActivityResultLaunchers 초기화
        getImageLaunchers = mutableListOf()
        cameraLaunchers = mutableListOf()

        // 현재시간
        val currentDateTime = SimpleDateFormat("yyyy.MM.dd\nHH:mm:ss", Locale.getDefault()).format(Date())

        for (i in 0 until 6) {
            // 갤러리에서 선택하기
            val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        recyclerViewAdapters[i].addItem(RecyclerViewItem(it, currentDateTime))
                    }
                }
            }
            getImageLaunchers.add(launcher)

            // 카메라로 사진 찍기
            val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && currentPhotoPath != null) {
                    val photoUri = Uri.fromFile(File(currentPhotoPath!!))
                    recyclerViewAdapters[i].addItem(RecyclerViewItem(photoUri, currentDateTime))
                }
            }
            cameraLaunchers.add(cameraLauncher)
        }


        // 리사이클러뷰 초기화
        recyclerViewAdapters = MutableList(6) { RecyclerViewAdapter(mutableListOf()) }

        binding.recyclerView1.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recyclerViewAdapters[0]
        }
        binding.recyclerView2.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recyclerViewAdapters[1]
        }
        binding.recyclerView3.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recyclerViewAdapters[2]
        }
        binding.recyclerView4.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recyclerViewAdapters[3]
        }
        binding.recyclerView5.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recyclerViewAdapters[4]
        }
        binding.recyclerView6.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recyclerViewAdapters[5]
        }

        // 각 이미지 버튼 클릭 이벤트 처리
        binding.imgBtnPhoto1.setOnClickListener { showOptionDialog(0) }
        binding.imgBtnPhoto2.setOnClickListener { showOptionDialog(1) }
        binding.imgBtnPhoto3.setOnClickListener { showOptionDialog(2) }
        binding.imgBtnPhoto4.setOnClickListener { showOptionDialog(3) }
        binding.imgBtnPhoto5.setOnClickListener { showOptionDialog(4) }
        binding.imgBtnPhoto6.setOnClickListener { showOptionDialog(5) }

        return binding.root
    }

    private fun showOptionDialog(position: Int) {
        val options = arrayOf("카메라로 사진 찍기", "갤러리에서 선택하기")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("사진 추가")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera(position)
                1 -> openGallery(position)
            }
        }
        builder.show()
    }

    private fun openGallery(position: Int) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        getImageLaunchers[position].launch(intent)
    }

    private fun openCamera(position: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile = try {
                createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
            photoFile?.also {
                val photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireActivity().packageName}.fileprovider",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                cameraLaunchers[position].launch(intent)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // 권한 거부 시 사용자에게 알림
    private fun showPermissionDeniedMessage() {
        Toast.makeText(requireContext(), "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
    }
}



