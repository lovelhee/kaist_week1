package com.example.madcampweek1.loginJh

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madcampweek1.ApplicationUserCode
import com.example.madcampweek1.R
import com.example.madcampweek1.userInfoDatabase.UserInfo
import com.example.madcampweek1.databinding.ActivityLoginJhBinding
import com.example.madcampweek1.databinding.DialogSignUpBinding
import com.example.madcampweek1.generalAppDatabase.GeneralAppDatabase
import com.example.madcampweek1.member.MemberActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginJhActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginJhBinding
    private lateinit var db: GeneralAppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginJhBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = GeneralAppDatabase.getInstance(this)

        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString()
            val password = binding.etPassWord.text.toString()
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    db.userInfoDao().getUserByCredentials(id, password)
                }
                if (user != null) {
                    Toast.makeText(this@LoginJhActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                    // ApplicationUserCode에 userCode 저장
                    val application = applicationContext as ApplicationUserCode
                    application.userCode = user.userCode

                    val intent = Intent(this@LoginJhActivity, MemberActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@LoginJhActivity, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.layoutMember.setOnClickListener {
            showSignUpDialog()
        }
    }

    private fun showSignUpDialog() {
        val dialogBinding = DialogSignUpBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("가입") { _, _ ->
                val id = dialogBinding.etNewId.text.toString()
                val password = dialogBinding.etNewPassword.text.toString()
                val confirmPassword = dialogBinding.etConfirmPassword.text.toString()
                val userType = when (dialogBinding.radioGroupUserType.checkedRadioButtonId) {
                    R.id.radioGeneralUser -> "일반회원"
                    R.id.radioAdjuster -> "손해사정사"
                    else -> null
                }

                if (id.isBlank() || password.isBlank() || userType == null) {
                    Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (password != confirmPassword) {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch {
                        val newUserInfo = UserInfo(id = id, password = password, userType = userType)
                        withContext(Dispatchers.IO) {
                            db.userInfoDao().insertUser(newUserInfo)
                        }
                        Toast.makeText(this@LoginJhActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("취소", null)
            .create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_signup_background)
        dialog.show()
    }
}