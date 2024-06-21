package com.rkdrndyd.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var userID_editText: EditText
    private lateinit var userPW_editText: EditText

    private lateinit var signIN_button: Button
    private lateinit var SignUp_Text: TextView

    private lateinit var autoLogin_checkBox: CheckBox

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var userID_SPFile: String
    private lateinit var userPW_SPFile: String
    private lateinit var autoLogin_SPFile: String

    private lateinit var LoginPWShowAndHide: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userID_editText = findViewById(R.id.LoginID)
        userPW_editText = findViewById(R.id.LoginPW)

        signIN_button = findViewById(R.id.LoginButton)
        SignUp_Text = findViewById(R.id.SignUpTextView)

        autoLogin_checkBox = findViewById(R.id.AutoLoginCheckBox)

        //앱 데이터 가져오기
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        userID_SPFile = sharedPreferences.getString("userID", "Empty")!!
        userPW_SPFile = sharedPreferences.getString("userPW", "Empty")!!
        autoLogin_SPFile = sharedPreferences.getString("autoLogin", "No")!!

        //자동 로그인
        if(autoLogin_SPFile.equals("Yes")) {
            Login(userID_SPFile, userPW_SPFile, true)
        }

        //비밀번호 숨기기 / 보이기
        LoginPWShowAndHide = findViewById(R.id.LoginPWShowAndHide)
        LoginPWShowAndHide.tag = "hiding"

        LoginPWShowAndHide.setOnClickListener {
            if (userPW_editText.text.toString() != "") {
                if (LoginPWShowAndHide.tag == "hiding") {
                    LoginPWShowAndHide.tag = "showing"
                    userPW_editText.inputType = InputType.TYPE_CLASS_TEXT
                    userPW_editText.setSelection(userPW_editText.text.length)
                    LoginPWShowAndHide.setImageResource(R.drawable.ic_hide)
                } else {
                    LoginPWShowAndHide.tag = "hiding"
                    userPW_editText.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    userPW_editText.setSelection(userPW_editText.text.length)
                    LoginPWShowAndHide.setImageResource(R.drawable.ic_show)
                }
            }
        }

        //로그인 버튼
        signIN_button.setOnClickListener {
            Login(userID_editText.text.toString(), userPW_editText.text.toString(), autoLogin_checkBox.isChecked)
        }
        
        //회원가입 버튼
        SignUp_Text.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    //로그인 함수
    private fun Login(userID : String, userPW : String, autoLogin : Boolean) {
        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")

                if (success) {
                    //받는 값
                    val userID_response = jsonObject.getString("userID")
                    val userName_response = jsonObject.getString("userName")
                    val userAge_response = jsonObject.getString("userAge")
                    val userUID_response = jsonObject.getString("uid")
                    val userPoint_response = jsonObject.getString("point")

                    //앱 데이터 저장
                    if(autoLogin) {
                        editor.putString("autoLogin", "Yes")
                    }
                    editor.putString("userID", userID_response)
                    editor.putString("userPW", userPW)
                    editor.putString("userName", userName_response)
                    editor.putString("userAge", userAge_response)
                    editor.apply()
                    
                    Toast.makeText(applicationContext, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "서버 응답 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        //전송 클래스 설정
        val loginRequest = LoginRequest(userID, userPW, responseListener)
        val queue = Volley.newRequestQueue(this@LoginActivity)
        queue.add(loginRequest)
    }
}
