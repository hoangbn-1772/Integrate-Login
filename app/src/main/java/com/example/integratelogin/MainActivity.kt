package com.example.integratelogin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.integratelogin.facebook.FacebookLoginActivity
import com.example.integratelogin.google.GoogleLoginActivity
import com.example.integratelogin.twitter.TwitterLoginActivity
import com.facebook.CallbackManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val callbackManager by lazy { CallbackManager.Factory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponents()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_fb_login -> moveScreen(FacebookLoginActivity::class.java)

            R.id.btn_gg_login -> moveScreen(GoogleLoginActivity::class.java)

            R.id.btn_twitter_login -> moveScreen(TwitterLoginActivity::class.java)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initComponents() {
        btn_fb_login?.setOnClickListener(this)

        btn_gg_login?.setOnClickListener(this)

        btn_twitter_login.setOnClickListener(this)
    }

    private fun moveScreen(screen: Class<*>) {
        Intent(this, screen).apply {
            startActivity(this)
        }
    }
}
