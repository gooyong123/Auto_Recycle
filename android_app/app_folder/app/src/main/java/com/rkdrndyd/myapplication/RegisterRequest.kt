package com.rkdrndyd.myapplication

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest


class RegisterRequest(
    userID: String,
    userPassword: String,
    userSalt: String,
    userName: String,
    userAge: Int,
    listener: Response.Listener<String>,
) :
    StringRequest(Method.POST, URL, listener, null) {

    private val map: MutableMap<String, String>

    init {
        map = HashMap()
        map["userID"] = userID
        map["userPassword"] = userPassword
        map["userSalt"] = userSalt
        map["userName"] = userName
        map["userAge"] = userAge.toString() + ""
    }

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String>? {
        return map
    }

    companion object {
        // 서버 URL 설정 ( PHP 파일 연동 )
        private const val URL = "http://3.38.227.45/Register.php"
    }
}