package com.example.partyfinder.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import com.example.partyfinder.R
import com.example.partyfinder.utils.CustomTextViewBold

class LoginActivity : Activity() {

    private lateinit var tvRegister : CustomTextViewBold
    private lateinit var vvLogin: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvRegister = findViewById(R.id.tv_register)
        vvLogin = findViewById(R.id.vv_login)

        tvRegister.setOnClickListener {

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        val videoPath = "android.resource://" + packageName + "/" + R.raw.party
        vvLogin.setVideoURI(Uri.parse(videoPath))
        vvLogin.setOnPreparedListener{ mediaPlayer ->
            val videoWidth = mediaPlayer.videoWidth
            val videoHeight = mediaPlayer.videoHeight

            val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels
            val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()

            val lp = vvLogin.layoutParams

            if (videoProportion > screenProportion) {
                lp.width = screenWidth
                lp.height = (screenWidth / videoProportion).toInt()
            } else {
                lp.width = (videoProportion * screenHeight).toInt()
                lp.height = screenHeight
            }

            vvLogin.layoutParams = lp
        }
        vvLogin.start()

        vvLogin.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.start()
        }
    }

    override fun onResume() {
        super.onResume()
        vvLogin.start()
    }

    override fun onPause() {
        super.onPause()
        vvLogin.pause()
    }

    override fun onStop() {
        super.onStop()
        vvLogin.stopPlayback()
    }
}