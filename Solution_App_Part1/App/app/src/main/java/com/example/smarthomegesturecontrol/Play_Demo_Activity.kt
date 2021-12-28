package com.example.smarthomegesturecontrol

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class Play_Demo_Activity : AppCompatActivity() {
    private var gesture_chosen: String? = null
    private var Video_View_Expert: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_expertvideo)
        Video_View_Expert = findViewById(R.id.gestureVideo)
        val `in` = intent
        gesture_chosen = `in`.getStringExtra("gesture_name")
        gestureToBePlayed = "h_" + gesture_chosen?.replace(" ".toRegex(), "_")?.toLowerCase(Locale.ROOT)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Video_View_Expert?.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        ExpertVideoPlayerRelease()
    }

    private fun initializePlayer() {
        val Video_uri_Expert = getExpertVideo(gestureToBePlayed)
        Video_View_Expert?.setVideoURI(Video_uri_Expert)
        Video_View_Expert?.start()
    }

    private fun getExpertVideo(mediaName: String?): Uri? {
        return Uri.parse("android.resource://" + packageName +
                "/raw/" + mediaName)
    }

    private fun ExpertVideoPlayerRelease() {
        Video_View_Expert?.stopPlayback()
    }

    fun replayExpertVideo(view: View?) {
        initializePlayer()
    }

    fun moveToPracticeRecordScreen(view: View?) {
        val Record_Practice = Intent(this@Play_Demo_Activity, Practice_Record_Activity::class.java)
        Record_Practice.putExtra("gesture_name", gesture_chosen)
        startActivity(Record_Practice)
    }

    companion object {
        private var gestureToBePlayed: String? = null
    }
}