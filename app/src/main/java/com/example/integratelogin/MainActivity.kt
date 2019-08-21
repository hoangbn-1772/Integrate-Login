package com.example.integratelogin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = MainActivity::class.java.simpleName

    private val callbackManager by lazy { CallbackManager.Factory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkLoginFacebook()) {
            moveScreen()
        }

        initComponents()

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                saveAccessToken(result?.accessToken.toString())
            }

            override fun onCancel() {
                showMessage("Login cancel")
            }

            override fun onError(error: FacebookException?) {
                showMessage(error?.localizedMessage.toString())
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_facebook_login -> onFacebookLogin()

            R.id.btn_custom_fb_login -> onCustomFacebookLogin()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initComponents() {
        btn_facebook_login?.setOnClickListener(this)
        btn_facebook_login?.setPermissions(permissions)

        btn_custom_fb_login?.setOnClickListener(this)
    }

    private fun checkLoginFacebook(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()

        return accessToken != null && !accessToken.isExpired
    }

    private fun onFacebookLogin() {
        btn_facebook_login?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d(TAG, result?.accessToken.toString())
                moveScreen()
            }

            override fun onCancel() {
                showMessage("Login cancel")
            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, error?.localizedMessage)
            }
        })
    }

    private fun onCustomFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, permissions)
    }

    private fun moveScreen() {
        startActivity(SecondActivity.getIntent(this, Profile.getCurrentProfile()))
    }

    private fun saveAccessToken(accessToken: String) {
        val spe = this.getPreferences(Context.MODE_PRIVATE).edit()
        spe.putString("token", accessToken)
        spe.apply()
    }

    private fun loadAccessToken(): AccessToken {
        val sp = this.getPreferences(Context.MODE_PRIVATE)
        val accessToken = sp.getString("token", "")
        return accessToken as AccessToken
    }

    private fun showMessage(msg: String) = Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()

    companion object {
        private const val PERMISSION_EMAIL = "email"
        private const val PERMISSION_PUBLIC_PROFILE = "public_profile"
        private const val PERMISSION_USER_FRIENDS = "user_friends"
        private const val PERMISSION_BIRTH_DAY = "user_birthday"

        private val permissions = arrayListOf(
            PERMISSION_EMAIL,
            PERMISSION_BIRTH_DAY,
            PERMISSION_PUBLIC_PROFILE,
            PERMISSION_USER_FRIENDS)

        val EXTRA_USER = "EXTRA_USER"
    }
}
