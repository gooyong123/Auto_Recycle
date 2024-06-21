package com.rkdrndyd.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64


class RegisterActivity : AppCompatActivity() {
    private lateinit var et_id: EditText
    private lateinit var et_pass: EditText
    private lateinit var et_pass_c: EditText
    private lateinit var et_name: EditText
    private lateinit var et_age: EditText
    private lateinit var btn_register: Button
    private lateinit var userSalt: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        et_id = findViewById(R.id.et_id)
        et_pass = findViewById(R.id.et_pass)
        et_pass_c = findViewById(R.id.et_pass_c)
        et_name = findViewById(R.id.et_name)
        et_age = findViewById(R.id.et_age)
        btn_register = findViewById(R.id.btn_register)

        btn_register.setOnClickListener {
            if (et_id.text.isEmpty() || et_pass.text.isEmpty() || et_name.text.isEmpty() || et_age.text.isEmpty()) {
                Toast.makeText(applicationContext, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if(!isValidPw(et_pass.text.toString())) {
                Toast.makeText(applicationContext, "비밀번호형식에 맞춰주세요. 특수문자,영어,숫자로 이루어진 8~16자(특수문자, 대문자 1개 이상)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if(!isNumeric(et_age.text.toString())) {
                Toast.makeText(applicationContext, "나이에 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if(et_pass.text.toString() != et_pass_c.text.toString()){
                Toast.makeText(applicationContext, "비밀번호가 서로 다릅니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                sendUserInfo()
            }
        }
    }

    fun isValidPw(pw: String): Boolean {
        val regex = Regex("^(?=.*[A-Z])(?=.*[\\W_])[A-Za-z\\d\\W_]{8,16}\$")
        return regex.matches(pw)
    }

    private fun isNumeric(str: String): Boolean {
        return str.toDoubleOrNull() != null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateSalt(length: Int = 16): String {
        val random = SecureRandom()
        val salt = ByteArray(length)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    private fun sha256(input: String, salt: String): String {
        val bytes = (input + salt).toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") {
            "%02x".format(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendUserInfo() {
        val userID = et_id.text.toString()
        val userPass = et_pass.text.toString()
        val userName = et_name.text.toString()
        val userAge = et_age.text.toString().toInt()

        userSalt = generateSalt()
        val hashPass = sha256(userPass, userSalt)

        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")
                val message = jsonObject.getString("message")
                if (success) {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "서버 응답 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val registerRequest = RegisterRequest(userID, hashPass, userSalt, userName, userAge, responseListener)
        val queue = Volley.newRequestQueue(this@RegisterActivity)
        queue.add(registerRequest)
    }
}
