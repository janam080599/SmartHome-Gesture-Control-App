package com.example.smarthomegesturecontrol

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.conscrypt.Conscrypt
import java.io.File
import java.io.IOException
import java.security.Security
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Practice_Record_Activity : AppCompatActivity() {
    private var slct_in: String? = null
    private var cnt_prc = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_record)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val i = intent
        slct_in = i.getStringExtra("gesture_name")
        gestureToRecord = gestureMap.get(slct_in)?.first as String

        findViewById<View?>(R.id.btnRecord).setOnClickListener { startPracticeRecording() }

        findViewById<View?>(R.id.btnUpload).setOnClickListener {
            Toast.makeText(applicationContext, "Sending post Request to Flask Server", Toast.LENGTH_LONG).show()
            postRequest()
        }
    }

    fun startPracticeRecording() {
        println("Start Recording")
        cnt_prc = gestureMap.get(slct_in)?.second as Int
        val recordedFile = File(getExternalFilesDir(null).toString() + "/" + gestureToRecord + "_PRACTICE_" + cnt_prc + "_VAIDYA.mp4")
        recordedFileName = recordedFile.absolutePath
        val i = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            i.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        } else {
            i.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }
        i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        i.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 4)
        val videoUri = FileProvider.getUriForFile(this, "com.example.smarthomegesturecontrol.provider", recordedFile)
        i.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
        startActivityForResult(i, VIDEO_CAPTURE)
    }

    private fun hasCamera(): Boolean {
        return packageManager.hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)
    }

    override fun onActivityResult(
            rqstCode: Int,
            rsltCode: Int, data: Intent?
    ) {
        super.onActivityResult(rqstCode, rsltCode, data)
        if (rqstCode == VIDEO_CAPTURE) {
            if (rsltCode == RESULT_OK) {
                Toast.makeText(this, """
     Practice Video has been saved to:
     ${data?.getData()}
     """.trimIndent(), Toast.LENGTH_LONG).show()
            } else if (rsltCode == RESULT_CANCELED) {
                Toast.makeText(this, "Recording Cancelled.",
                        Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Record Video Process Failed",
                        Toast.LENGTH_LONG).show()
            }
        }
        cnt_prc++

        gestureMap.put(slct_in, gestureMap.get(slct_in)?.copy(second = cnt_prc))
    }

    fun postRequest() {
        Security.insertProviderAt(Conscrypt.newProvider(), 1)
    Toast.makeText(this, "String nma"+ recordedFileName,
            Toast.LENGTH_LONG).show()
        val arrD: Array<String?> = recordedFileName?.split("/".toRegex())!!.toTypedArray()
        val practiceFileName = arrD[8]
        val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", practiceFileName, File(recordedFileName).asRequestBody("video/mp4".toMediaTypeOrNull())).build()
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url("http://192.168.0.110:9000/uploader").post(requestBody).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
                runOnUiThread { Toast.makeText(applicationContext, "Something went wrong:" + " ", Toast.LENGTH_SHORT).show() }
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    try {
                        val response_body = response.body?.string()
                        println(response_body)
                        Toast.makeText(applicationContext, response_body, Toast.LENGTH_LONG).show()
                        try {
                            Thread.sleep(2000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        val gotoMainActivity = Intent(this@Practice_Record_Activity, MainActivity::class.java)
                        startActivity(gotoMainActivity)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    companion object {
        private const val VIDEO_CAPTURE = 101
        private var recordedFileName: String? = null
        private var gestureToRecord: String? = null
        private val gestureMap: HashMap<String?, Pair<String, Int>?> = object : HashMap<String?, Pair<String, Int>?>() {
            init {
                put("Select a Gesture", Pair("SelectGesture", 1))
                put("Turn On Lights", Pair("LightOn", 1))
                put("Turn Off Lights", Pair("LightOff", 1))
                put("Turn On Fan", Pair("FanOn", 1))
                put("Turn Off Fan", Pair("FanOff", 1))
                put("Increase Fan Speed", Pair("FanUp", 1))
                put("Decrease Fan Speed", Pair("FanDown", 1))
                put("Set Thermostat to specified temperature", Pair("SetThermo", 1))
                put("0", Pair("Num0", 1))
                put("1", Pair("Num1", 1))
                put("2", Pair("Num2", 1))
                put("3", Pair("Num3", 1))
                put("4", Pair("Num4", 1))
                put("5", Pair("Num5", 1))
                put("6", Pair("Num6", 1))
                put("7", Pair("Num7", 1))
                put("8", Pair("Num8", 1))
                put("9", Pair("Num9", 1))
            }
        }
    }
}