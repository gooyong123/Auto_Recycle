package com.rkdrndyd.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class IntroActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var userID_SPFile: String
    private lateinit var userPW_SPFile: String
    private lateinit var autoLogin_SPFile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        //앱 데이터 가져오기
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        userID_SPFile = sharedPreferences.getString("userID", "Empty")!!
        userPW_SPFile = sharedPreferences.getString("userPW", "Empty")!!
        autoLogin_SPFile = sharedPreferences.getString("autoLogin", "No")!!


        // 일정 시간 후 LoginActivity로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            //자동 로그인
            if(autoLogin_SPFile.equals("Yes")) {
                Login(userID_SPFile, userPW_SPFile, true)
            } else {
                val intent = Intent(this@IntroActivity, LoginActivity::class.java)
                startActivity(intent)
                finish() // IntroActivity를 종료하여 뒤로 가기 버튼을 눌렀을 때 다시 나타나지 않도록 함
            }
        }, 3000) // 3000 밀리초 = 3초
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
                    val intent = Intent(this@IntroActivity, MainActivity::class.java)
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
        val queue = Volley.newRequestQueue(this@IntroActivity)
        queue.add(loginRequest)
    }
}
