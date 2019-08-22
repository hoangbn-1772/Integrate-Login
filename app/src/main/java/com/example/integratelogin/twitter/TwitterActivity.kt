package com.example.integratelogin.twitter

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.integratelogin.R
import kotlinx.android.synthetic.main.activity_twitter.*

class TwitterActivity : AppCompatActivity() {

    private val user by lazy { intent.getParcelableExtra<User>(TwitterLoginActivity.EXTRAS_USER) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)
        if (user != null) {
            updateUi()
        }
    }

    private fun updateUi() {
        txt_name_tw?.text = user.name
        txt_screen_name_tw?.text = user.screenName
        Glide.with(this)
            .load(Uri.parse(user.profileImageUrl))
            .error(R.drawable.ic_launcher_background)
            .into(img_avatar_tw)
    }

    companion object {

        fun getIntent(context: Context, user: User?): Intent {
            val intent = Intent(context, TwitterActivity::class.java)
            val bundle = Bundle().apply {
                putParcelable(TwitterLoginActivity.EXTRAS_USER, user)
            }
            return intent.putExtras(bundle)
        }
    }
}
