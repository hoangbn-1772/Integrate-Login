package com.example.integratelogin.google

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.integratelogin.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_google_login.*

class GoogleLoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login)
        initComponents()
    }

    override fun onStart() {
        super.onStart()
        checkLoginStatus()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_google_login_default -> signIn()

            R.id.btn_google_login_custom -> signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent()
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                // The Task returned from this call is always completed, no need to attach a listener
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }

            RESULT_LOGOUT -> signOut()
        }
    }

    private fun initComponents() {
        createGoogleSignIn()
        createUI()
    }

    private fun createGoogleSignIn() {
        // Configure sign-in to request the user's ID, email, profile.
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .requestProfile()
            .build()


        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun createUI() {
        btn_google_login_default?.apply {
            setSize(SignInButton.SIZE_STANDARD)
            setStyle(1, 1)
            setOnClickListener(this@GoogleLoginActivity)
        }

        btn_google_login_custom?.setOnClickListener(this)
    }

    private fun checkLoginStatus() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            // update UI: hide the sign-in button, launch your main activity.
        } else {
            // Display the Google Sign-in button
        }
    }

    private fun signIn() {
        // Start flow sign-in
        mGoogleSignInClient.signInIntent.apply {
            startActivityForResult(this, REQUEST_CODE_SIGN_IN)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI
            // updateUI
            account?.let { moveMain(it) }
        } catch (e: ApiException) {
            Log.w(TAG, e.statusCode.toString())
            // updateUI
        }
    }

    private fun moveMain(account: GoogleSignInAccount) {
        startActivityForResult(GoogleActivity.getIntent(this, account), RESULT_LOGOUT)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            Log.d(TAG, "Sign out: ${it.isSuccessful}")
        }
    }

    private fun disconnectAccount() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener {
            Log.d(TAG, "Disconnect Account: ${it.isSuccessful}")
        }
    }

    companion object {
        private val TAG = GoogleLoginActivity::class.java.simpleName
        private const val REQUEST_CODE_SIGN_IN = 100
        const val EXTRAS_ACCOUNT = "EXTRAS_ACCOUNT"
        const val RESULT_LOGOUT = 101
    }
}
