// HistoryActivity.kt
package com.rkdrndyd.myapplication

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HistoryActivity : AppCompatActivity() {
    private lateinit var noHistoryTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userIDSPFile: String
    private lateinit var recyclerView: RecyclerView

    private lateinit var emptyList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history__layout)

        emptyList = emptyList()

        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        userIDSPFile = sharedPreferences.getString("userID", "Empty")!!

        noHistoryTextView = findViewById(R.id.noHistoryTextView)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        startRepeatingTask()
    }

    private fun startRepeatingTask() {
        lifecycleScope.launch {
            while (isActive) {
                showHistory(object : HistoryCallback {
                    override fun onSuccess(logList: List<String>) {
                        if (logList.isNotEmpty()) {
                            recyclerView.adapter = HistoryAdapter(logList)
                            noHistoryTextView.visibility = View.GONE
                        } else {
                            noHistoryTextView.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(message: String) {
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                })
                delay(1000L) // 1초 지연
            }
        }
    }

    private fun showHistory(callback: HistoryCallback) {
        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")
                if (success) {
                    val logs = jsonObject.getJSONArray("itemList")
                    val logList = jsonArrayToList(logs)
                    Log.i("HistoryActivity", "$logList")
                    callback.onSuccess(logList)
                } else {
                    callback.onSuccess(emptyList)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                callback.onFailure("통신 중 오류가 발생했습니다.")
                Log.i("HistoryActivity", "Error parsing JSON")
            }
        }
        val showHistory = ShowHistoryRequest(userIDSPFile, responseListener)
        val queue = Volley.newRequestQueue(this@HistoryActivity)
        queue.add(showHistory)
    }

    private fun jsonArrayToList(jsonArray: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            // JSON 객체로부터 history 키의 값을 추출
            val history = jsonObject.getString("history")
            list.add(history)
        }
        return list
    }
}

interface HistoryCallback {
    fun onSuccess(logList: List<String>)
    fun onFailure(message: String)
}
