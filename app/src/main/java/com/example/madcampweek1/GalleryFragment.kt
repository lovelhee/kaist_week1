package com.example.madcampweek1

import android.Manifest
import android.app.Activity
import android.content.Context
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
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcampweek1.databinding.FragmentGalleryBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.lifecycleScope
import com.example.madcampweek1.GalleryDataBase.GalleryImage
import com.example.madcampweek1.GalleryDataBase.GalleryImageDao
import com.example.madcampweek1.generalAppDatabase.GeneralAppDatabase
import com.example.madcampweek1.member.User
import com.example.madcampweek1.userInfoDatabase.UserInfo
import com.example.madcampweek1.userInfoDatabase.UserInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GalleryFragment : Fragment(R.layout.fragment_gallery) {
    private lateinit var binding: FragmentGalleryBinding

    // 리사이클러뷰 어댑터
    private lateinit var recyclerViewAdapters: MutableList<RecyclerViewAdapter>

    // 갤러리
    private lateinit var getImageLaunchers: MutableList<ActivityResultLauncher<Intent>>

    // 카메라
    private lateinit var cameraLaunchers: MutableList<ActivityResultLauncher<Intent>>
    private var currentPhotoPath: String? = null

    // DB
    private lateinit var galleryImageDao: GalleryImageDao
    private lateinit var userInfoDao: UserInfoDao

    // 권한
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    // userCode
    private var userCode: Int = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)

        // DB 초기화
        val db = GeneralAppDatabase.getInstance(requireContext())
        galleryImageDao = db.galleryImageDao()
        userInfoDao = db.userInfoDao()

        // UserCode
        val application = requireActivity().application as ApplicationUserCode
        userCode = application.userCode

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
        val currentDateTime = SimpleDateFormat("yyyy.MM.dd\n  HH:mm:ss", Locale.getDefault()).format(Date())

        for (i in 0 until 6) {
            // 갤러리에서 선택하기
            val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        // 리사이클러뷰 어댑터에 추가
                        recyclerViewAdapters[i].addItem(RecyclerViewItem(it, currentDateTime))

                        // DB에 추가
                        val galleryImage = GalleryImage(userCode= userCode, imageUri = it.toString(), timestamp = currentDateTime, position = i)
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                            try {
                                galleryImageDao.insert(galleryImage)
                                Log.d("GalleryFragment", "Successfully added to database: $galleryImage")
                            } catch (e: Exception) {
                                Log.e("GalleryFragment", "Error inserting image into database", e)
                            }
                        }
                    }
                }
            }
            getImageLaunchers.add(launcher)

            // 카메라로 사진 찍기
            val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && currentPhotoPath != null) {
                    val photoUri = Uri.fromFile(File(currentPhotoPath!!))
                    // 리사이클러뷰 어댑터에 추가
                    recyclerViewAdapters[i].addItem(RecyclerViewItem(photoUri, currentDateTime))

                    // DB에 추가
                    val galleryImage = GalleryImage(userCode=userCode, imageUri = photoUri.toString(), timestamp = currentDateTime, position = i)
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
                        galleryImageDao.insert(galleryImage)
                    }
                }
            }
            cameraLaunchers.add(cameraLauncher)
        }


        // 리사이클러뷰 초기화
        recyclerViewAdapters = MutableList(6) { _ -> RecyclerViewAdapter(mutableListOf(), galleryImageDao, viewLifecycleOwner.lifecycleScope) }

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

        // DB에서 이미지 불러오기
        loadImagesFromDatabase()

        // 각 이미지 버튼 클릭 이벤트 처리
        binding.imgBtnPhoto1.setOnClickListener { showOptionDialog(0) }
        binding.imgBtnPhoto2.setOnClickListener { showOptionDialog(1) }
        binding.imgBtnPhoto3.setOnClickListener { showOptionDialog(2) }
        binding.imgBtnPhoto4.setOnClickListener { showOptionDialog(3) }
        binding.imgBtnPhoto5.setOnClickListener { showOptionDialog(4) }
        binding.imgBtnPhoto6.setOnClickListener { showOptionDialog(5) }

        return binding.root
    }

    private fun loadImagesFromDatabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            // DB에서 모든 이미지 로드
            val userAllImages = galleryImageDao.getAllImagesByUserCode(userCode)

            // 이미지를 RecyclerView 어댑터에 추가
            userAllImages.forEach(){ galleryImage ->
                val imageUri = Uri.parse(galleryImage.imageUri)
                val timestamp = galleryImage.timestamp
                val position = galleryImage.position
                recyclerViewAdapters[position].addItem(RecyclerViewItem(imageUri, timestamp))
            }
        }
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



