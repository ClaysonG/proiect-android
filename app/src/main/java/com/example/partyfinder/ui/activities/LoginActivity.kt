package com.example.partyfinder.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tvRegister : CustomTextViewBold
    private lateinit var vvLogin: VideoView
    private lateinit var etEmail: CustomEditText
    private lateinit var etPassword: CustomEditText
    private lateinit var tvForgotPassword: CustomTextView
    private lateinit var btnLogin: CustomButton
    private lateinit var btnGoogleLogin: CustomButton

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLoginStatus()

        setContentView(R.layout.activity_login)

        tvRegister = findViewById(R.id.tv_register)
        vvLogin = findViewById(R.id.vv_login)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        btnLogin = findViewById(R.id.btn_login)
        btnGoogleLogin = findViewById(R.id.btn_google_login)

        tvForgotPassword.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
        tvRegister.setOnClickListener(this)
        btnGoogleLogin.setOnClickListener(this)

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

                R.id.btn_google_login -> {

                    loginWithGoogle()
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

    @Suppress("DEPRECATION")
    private fun loginWithGoogle() {

        showProgressDialog(resources.getString(R.string.please_wait))

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, googleSignInOptions)

        val intent: Intent = googleSignInClient.signInIntent

        startActivityForResult(intent, Constants.GOOGLE_SIGN_IN_CODE)
    }

    fun userLoggedInSuccess(user: User) {

        hideProgressDialog()

        if (user.profileCompleted == 0) {

            // If the user profile is incomplete then launch the EditUserProfileActivity.
            val intent = Intent(this@LoginActivity, EditUserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {

            // Redirect the user to Main Screen after log in.
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    private fun checkLoginStatus() {

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {

            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GOOGLE_SIGN_IN_CODE) {

            if (resultCode == RESULT_OK) {

                val signInAccountTask: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

                if (signInAccountTask.isSuccessful) {

                    try {

                        val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)

                        if (googleSignInAccount != null) {

                            val authCredential: AuthCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)

                            FirebaseAuth.getInstance().signInWithCredential(authCredential)
                                .addOnCompleteListener { task ->

                                    if (task.isSuccessful) {

                                        val firebaseUser: FirebaseUser = task.result!!.user!!

                                        val user = User(
                                            firebaseUser.uid,
                                            firebaseUser.displayName!!.split(" ")[0],
                                            firebaseUser.displayName!!.split(" ")[1],
                                            firebaseUser.email!!,
                                        )

                                        FirestoreClass().userExists(firebaseUser.uid) { exists ->

                                                if (!exists) {

                                                    FirestoreClass().registerUser(this@LoginActivity, user)
                                                } else {

                                                    FirestoreClass().getUserDetails(this@LoginActivity)
                                                }
                                        }
                                    } else {

                                        hideProgressDialog()
                                        showErrorSnackBar(task.exception!!.message.toString(), true)
                                    }
                                }
                        }
                    } catch (e: ApiException) {

                        hideProgressDialog()
                        showErrorSnackBar(e.message.toString(), true)
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {

                hideProgressDialog()
            }
        }
    }
}