package com.rkdrndyd.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import kotlin.properties.Delegates

class MyInfo : AppCompatActivity() {
    private lateinit var userPassword: String
    private lateinit var userPasswordCheck: String
    private lateinit var userID: String
    private lateinit var userName: String
    private var userAge by Delegates.notNull<Int>()

    private lateinit var logoutButton: Button
    private lateinit var quitButton: Button
    private lateinit var changeButton: Button

    private lateinit var myPWtext: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        val sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        logoutButton = findViewById(R.id.logout_button)
        quitButton = findViewById(R.id.quit_button)
        changeButton = findViewById(R.id.change_button)

        var changeAutoLogin = findViewById<TextView>(R.id.auto_login)
        var myName = findViewById<EditText>(R.id.my_name)
        var myAge= findViewById<EditText>(R.id.my_age)
        var myID = findViewById<TextView>(R.id.my_id)
        var myPW = findViewById<EditText>(R.id.my_pw)
        var myPWC = findViewById<EditText>(R.id.my_pw_c)
        myName.setText(sharedPreferences.getString("userName", "값을 가져오지 못했습니다.")!!)
        myAge.setText(sharedPreferences.getString("userAge", "값을 가져오지 못했습니다.")!!)
        myID.text = sharedPreferences.getString("userID", "값을 가져오지 못했습니다.")!!
        myPWtext = sharedPreferences.getString("userPW", "값을 가져오지 못했습니다.")!!
        changeAutoLogin.text = sharedPreferences.getString("autoLogin", "No")!!

        changeAutoLogin.setOnClickListener{
            if(changeAutoLogin.text.equals("Yes")){
                changeAutoLogin.text = "No"
                editor.putString("autoLogin", "No")
                editor.apply()
            }else{
                changeAutoLogin.text = "Yes"
                editor.putString("autoLogin", "Yes")
                editor.apply()
            }
        }

        //회원 정보 변경 부분
        changeButton.setOnClickListener {
            userID = myID.text.toString()
            userName = myName.text.toString()
            userAge = myAge.text.toString().toInt()
            userPassword = myPW.text.toString()
            userPasswordCheck = myPWC.text.toString()
            /*
            Toast.makeText(applicationContext, "$userID//$userPassword//$userPasswordCheck", Toast.LENGTH_SHORT).show();
            Toast.makeText(applicationContext, "$userName//$userAge", Toast.LENGTH_SHORT).show();
            */
            if (userPassword != userPasswordCheck) {
                Toast.makeText(applicationContext,"비밀번호가 서로 다릅니다.", Toast.LENGTH_SHORT).show()
            } else {
                ChangeInfo()
            }
        }

        logoutButton.setOnClickListener {
            editor.putString("autoLogin", "No")
            editor.apply()
            val intent = Intent(this@MyInfo, LoginActivity::class.java)
            startActivity(intent)
        }

        quitButton.setOnClickListener {
            userID = myID.text.toString()
            /// 리스트 선언
            val params = listOf(
                "userID" to userID,
                "URLendPoint" to "Quit.php"
            )
            AllForOne(params)
            editor.putString("autoLogin", "No")
            editor.apply()
            val intent = Intent(this@MyInfo, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    //통신 함수
    private fun AllForOne(List: List<Pair<String, String>>) {
        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")

                if (success) {
                    Toast.makeText(applicationContext, "회원탈퇴 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "회원탈퇴 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "서버 응답 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        //전송 클래스 설정
        val AllForOne = AllForOneRequest(List, responseListener)
        val queue = Volley.newRequestQueue(this@MyInfo)
        queue.add(AllForOne)
    }

    private fun ChangeInfo() {
        /*
        Toast.makeText(applicationContext, "$userID//$userPassword//$userPasswordCheck", Toast.LENGTH_SHORT).show();
        Toast.makeText(applicationContext, "$userName//$userAge", Toast.LENGTH_SHORT).show();
        */
        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")

                if (success) {
                    val userID = jsonObject.getString("userID")
                    val userName = jsonObject.getString("userName")
                    val userAge = jsonObject.getString("userAge")

                    val sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("userID", userID)
                    editor.putString("userName", userName)
                    editor.putString("userAge", userAge)
                    editor.putString("userPW", userPassword)
                    editor.apply()

                    Toast.makeText(applicationContext, "회원 정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MyInfo, MainActivity::class.java)
                    intent.putExtra("userID", userID)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "회원 정보 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "서버 응답 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        val RegisterMyInfo = MyInfoRequest(userID, userPassword, userName, userAge, responseListener)
        val queue = Volley.newRequestQueue(this@MyInfo)
        queue.add(RegisterMyInfo)
    }
    /*
    override fun onBackPressed() {
        super.onBackPressed()
        showBackSpaceConfirmationDialog()
    }

    private fun showBackSpaceConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("뒤로가기")
            .setMessage("변경을 취소하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("취소", null)
            .show()
    }
     */
}