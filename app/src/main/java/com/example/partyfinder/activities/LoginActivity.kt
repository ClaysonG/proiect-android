package com.example.partyfinder.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.VideoView
import com.example.partyfinder.R
import com.example.partyfinder.firestore.FirestoreClass
import com.example.partyfinder.models.User
import com.example.partyfinder.utils.Constants
import com.example.partyfinder.utils.CustomButton
import com.example.partyfinder.utils.CustomEditText
import com.example.partyfinder.utils.CustomTextView
import com.example.partyfinder.utils.CustomTextViewBold
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tvRegister : CustomTextViewBold
    private lateinit var vvLogin: VideoView
    private lateinit var etEmail: CustomEditText
    private lateinit var etPassword: CustomEditText
    private lateinit var tvForgotPassword: CustomTextView
    private lateinit var btnLogin: CustomButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvRegister = findViewById(R.id.tv_register)
        vvLogin = findViewById(R.id.vv_login)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        btnLogin = findViewById(R.id.btn_login)

        tvForgotPassword.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
        tvRegister.setOnClickListener(this)

        loadVideo()
    }

    private fun loadVideo() {

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

    override fun onClick(view: View?) {

        if (view != null) {
            when (view.id) {

                R.id.tv_forgot_password -> {

                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {

                    loginRegisteredUser()
                }

                R.id.tv_register -> {

                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {

        return when {
            TextUtils.isEmpty(etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                // showErrorSnackBar("Your input is valid.", false)
                true
            }
        }
    }

    private fun loginRegisteredUser() {

        if (validateLoginDetails()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space
            val email = etEmail.text.toString().trim { it <= ' ' }
            val password = etPassword.text.toString().trim { it <= ' ' }

            // Login using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        FirestoreClass().getUserDetails(this@LoginActivity)
                    } else {

                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user: User) {

        hideProgressDialog()

        if (user.profileCompleted == 0) {

            // If the user profile is incomplete then launch the UserProfileActivity.
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {

            // Redirect the user to Main Screen after log in.
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}