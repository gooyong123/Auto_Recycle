package com.rkdrndyd.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var Point: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var userIDSPFile: String
    private var oldPoint: Int = 0

    private lateinit var select_item: Button
    private lateinit var my_info: Button
    private lateinit var history: Button

    private lateinit var q_mark: ImageView

    private lateinit var bottomSheet: BottomSheetDialog

    private lateinit var itemAdapter: ItemAdapter
    private var itemList = ArrayList<String>()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Point = findViewById(R.id.PointTextView)
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        userIDSPFile = sharedPreferences.getString("userID", "Empty")!!

        q_mark = findViewById(R.id.q_mark_image_view)

        // 나중에 php로 서버 데이터에서 받기
        itemList.add("의정부시-001")
        itemList.add("의정부시-002")
        itemList.add("양주시-001")
        itemList.add("동두천시-004")
        itemList.add("동두천시-005")
        itemList.add("양주시-004")
        itemList.add("양주시-005")
        itemList.add("동두천시-001")
        itemList.add("동두천시-002")
        itemList.add("동두천시-003")
        itemList.add("양주시-003")
        itemList.add("의정부시-003")
        itemList.add("의정부시-004")
        itemList.add("의정부시-005")
        itemList.add("양주시-002")

        itemList.sort()
        /*
        itemList.add("001")
        itemList.add("002")
        itemList.add("003")
        itemList.add("004")
        itemList.add("005")
        */
        select_item = findViewById(R.id.select_item)
        select_item.setOnClickListener {
            showBottomSheet()
        }

        history = findViewById(R.id.hitoryButton)
        history.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            startActivity(intent)
        }

        my_info = findViewById(R.id.myInfo_button)
        my_info.setOnClickListener {
            val intent = Intent(this@MainActivity, MyInfo::class.java)
            startActivity(intent)
        }

        startRepeatingTask()

        q_mark.setOnClickListener{
            showCustomDialog()
        }
    }

    private fun showCustomDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_layout, null)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Activity 종료 시 코루틴 정리
    }

    private fun startRepeatingTask() {
        coroutineScope.launch {
            while (isActive) {
                showPoint(userIDSPFile)
                delay(1000L) // 1초 지연
            }
        }
    }

    private fun showPoint(userID: String) {
        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")
                if (success) {
                    val userPointResponse = jsonObject.getString("point")
                    Point.text = userPointResponse


                    sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
                    editor = sharedPreferences.edit()
                    oldPoint = sharedPreferences.getInt("oldPoint", 0)

                    var newPoint = userPointResponse.toInt()

                    if(newPoint != oldPoint) {
                        Toast.makeText(applicationContext,"포인트가 갱신되었습니다.", Toast.LENGTH_SHORT).show()
                        editor.putInt("oldPoint", newPoint)
                        editor.apply()
                    }

                } else {
                    //Toast.makeText(applicationContext, "포인트 갱신에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "포인트 갱신 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        val showPointRequest = ShowPoint(userID, responseListener)
        val queue = Volley.newRequestQueue(this@MainActivity)
        queue.add(showPointRequest)
    }
    private fun showBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        bottomSheet = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialogTheme)
        bottomSheet.setContentView(dialogView)

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.items)
        itemAdapter = ItemAdapter(itemList, object : ItemAdapter.OnItemClickListener {
            override fun onItemClick(item: String) {
                // 클릭된 아이템 처리
                Toast.makeText(this@MainActivity, "$item 선택", Toast.LENGTH_SHORT).show()
                sendUserIDToServer(item)
            }
        })
        recyclerView.adapter = itemAdapter

        val searchBar = dialogView.findViewById<EditText>(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                itemAdapter.filter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        bottomSheet.show()
    }

    private fun sendUserIDToServer(item: String) {
        val serverUrl = "http://3.38.227.45:8080/user"
        val userID = userIDSPFile
        coroutineScope.launch {
            delay(5000L) // 5초 지연

            sendRequestToServer(serverUrl, userID, item)
            //SaveLog(40,item) //파이 서버 안켰을때 씀.
        }
    }

    private suspend fun sendRequestToServer(serverUrl: String, userID: String, selectedOption: String) = withContext(
        Dispatchers.IO) {
        try {
            val url = URL(serverUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doOutput = true

            val postData = "userID=$userID&option=$selectedOption"
            val outputStream = connection.outputStream
            outputStream.write(postData.toByteArray(Charsets.UTF_8))
            outputStream.flush()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                bufferedReader.close()
                inputStream.close()

                // JSON 응답 파싱
                val jsonResponse = JSONObject(response.toString())
                val success = jsonResponse.getBoolean("success")
                if (success) {
                    val totalPoint = jsonResponse.getInt("total_point")
                    val addPoint = jsonResponse.getInt("add_point")

                    withContext(Dispatchers.Main) {
                        // UI 업데이트
                        Point.text = totalPoint.toString()
                        Toast.makeText(this@MainActivity, "$addPoint 만큼 적립되었습니다.", Toast.LENGTH_SHORT).show()
                        SaveLog(addPoint, selectedOption)
                        bottomSheet.dismiss() // 바텀시트를 닫습니다.
                    }
                } else {
                    val sql_pi = jsonResponse.getString("sql_pi")
                    val message = jsonResponse.getString("message")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "서버 응답 실패: $sql_pi", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "서버 요청 실패: $responseCode", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "서버 요청 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun SaveLog(addPoint: Int, item: String) {
        val logFile = File(getExternalFilesDir(null), "History.txt")
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        //val location = item
        val (location, num) = item.split("-") // 쉼표(,)를 기준으로 나누기
        Log.d("@@@@@@@@@@@@@@@@@@@@SaveLog", "이건가?: $location")
        try {
            FileWriter(logFile, true).use { writer -> // true = 새 내용 추가
                //writer.append("$timestamp//$location//$addPoint\n")
                var history = "$timestamp//$location//$addPoint\n"
                writeHistory(userIDSPFile, history)
            }
        } catch (e: IOException) {
            Log.e("ExampleClass", "실패실패실패실패실패실패실패실패실패실패실패실패실패실패실패실패", e)
        }
    }

    //로그인 함수
    private fun writeHistory(userID : String, history : String) {
        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")

                if (success) {
                    Toast.makeText(applicationContext, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "서버 응답 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        //전송 클래스 설정
        val writeHistory = WriteHisrtoyRequest(userID, history, responseListener)
        val queue = Volley.newRequestQueue(this@MainActivity)
        queue.add(writeHistory)
    }
}