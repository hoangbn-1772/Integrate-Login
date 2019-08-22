package com.example.integratelogin.twitter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.integratelogin.R
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User
import kotlinx.android.synthetic.main.activity_twitter_login.*

class TwitterLoginActivity : AppCompatActivity() {

    private lateinit var mTwitterAuthClient: TwitterAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTwitter()
        setContentView(R.layout.activity_twitter_login)

        mTwitterAuthClient = TwitterAuthClient()

        btn_login_twitter?.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                // Login successfully
                val session = TwitterCore.getInstance().sessionManager.activeSession
                val authToken = session.authToken
                Log.d(TAG, "Token: ${authToken.token}, Secret: ${authToken.secret}")

                result?.let { getUserProfile(it.data) }

            }

            override fun failure(exception: TwitterException?) {
                Log.d(TAG, exception.toString())
            }
        }

        btn_login_twitter_custom.setOnClickListener {
            customLogin()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result to the login button.
        btn_login_twitter?.onActivityResult(requestCode, resultCode, data)
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
    }

    private fun initTwitter() {
        // Optional
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(
                getString(R.string.api_key_twitter),
                getString(R.string.api_secret_key_twitter))
            )
            .debug(true)
            .build()

        Twitter.initialize(config)
    }

    private fun customLogin() {
        mTwitterAuthClient.authorize(this, object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                // Login successfully
                val session = TwitterCore.getInstance().sessionManager.activeSession
                val authToken = session.authToken
                Log.d(TAG, "Token: ${authToken.token}, Secret: ${authToken.secret}")

                result?.let { getUserProfile(it.data) }
            }

            override fun failure(exception: TwitterException?) {
                Log.d(TAG, exception.toString())
            }
        })
    }

    private fun getUserProfile(twitterSession: TwitterSession) {
        TwitterCore.getInstance().getApiClient(twitterSession).accountService
            .verifyCredentials(true, true, true)
            .enqueue(object : Callback<User>() {
                override fun success(result: Result<User>?) {
                    gotoMain(result?.data)
                }

                override fun failure(exception: TwitterException?) {
                    Log.d(TAG, exception.toString())
                }
            })
    }

    private fun gotoMain(user: User?) {
        val userCus = user?.let { User(it.name, it.screenName, it.profileImageUrl) }
        startActivityForResult(TwitterActivity.getIntent(this, userCus), RESULT_CODE_LOGOUT)
    }

    private fun logout() {
        TwitterCore.getInstance().sessionManager.clearActiveSession()
    }

    private fun requestEmail(session: TwitterSession) {
        val authClient = TwitterAuthClient()
        authClient.requestEmail(session, object : Callback<String>() {
            override fun success(result: Result<String>?) {

            }

            override fun failure(exception: TwitterException?) {
                Log.d(TAG, exception.toString())
            }
        })
    }

    companion object {
        private val TAG = TwitterLoginActivity::class.java.simpleName

        const val EXTRAS_USER = "EXTRAS_USER"

        const val RESULT_CODE_LOGOUT = 101
    }
}
