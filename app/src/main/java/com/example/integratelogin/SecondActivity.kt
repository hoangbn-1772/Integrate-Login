package com.example.integratelogin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity(), View.OnClickListener {

    private val user by lazy { intent.getParcelableExtra<Profile>(MainActivity.EXTRA_USER) }

    private val callbackManager by lazy { CallbackManager.Factory.create() }

    private lateinit var accessTokenTracker: AccessTokenTracker

    private lateinit var profileTracker: ProfileTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        initComponents()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> {
                LoginManager.getInstance().logOut()
                finish()
            }
        }
    }

    private fun initComponents() {
        btn_logout?.setOnClickListener(this)
        setUserProfile()
        trackingToken()
    }

    @SuppressLint("SetTextI18n")
    private fun setUserProfile() {
        txt_username?.text = "${user.firstName} ${user.middleName} ${user.lastName}"

        Glide.with(this)
            .load(user.getProfilePictureUri(100, 100))
            .error(R.drawable.ic_launcher_background)
            .into(img_avatar)
    }

    private fun trackingToken() {
        accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken?, currentAccessToken: AccessToken?) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        }
        accessTokenTracker.startTracking()
        // If the access token is available already assign it.
        val accessToken = AccessToken.getCurrentAccessToken()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        accessTokenTracker.stopTracking()
        profileTracker.stopTracking()
    }

    private fun trackingProfile() {
        profileTracker = object : ProfileTracker() {
            override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                // Implements
            }
        }
        profileTracker.startTracking()
    }

    companion object {

        fun getIntent(context: Context, user: Profile): Intent {
            val intent = Intent(context, SecondActivity::class.java)
            val bundle = Bundle().apply {
                putParcelable(MainActivity.EXTRA_USER, user)
            }
            intent.putExtras(bundle)
            return intent
        }
    }
}
