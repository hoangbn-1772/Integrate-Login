package com.example.integratelogin.google

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.integratelogin.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.activity_google.*

class GoogleActivity : AppCompatActivity(), View.OnClickListener {

    private val account by lazy {
        intent.getParcelableExtra<GoogleSignInAccount>(GoogleLoginActivity.EXTRAS_ACCOUNT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)
        initComponents()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_google_logout -> {
                Intent().apply {
                    setResult(GoogleLoginActivity.RESULT_LOGOUT, this)
                    finish()
                }
            }
        }
    }

    private fun initComponents() {
        setupUI()
        btn_google_logout?.setOnClickListener(this)
    }

    private fun setupUI() {
        txt_username?.text = account.displayName
        txt_mail?.text = account.email
        Glide.with(this)
            .load(account.photoUrl)
            .error(R.drawable.ic_launcher_background)
            .into(img_avatar)

        Log.d("TAG", "${account.givenName}, ${account.familyName}, ${account.account}")
    }

    companion object {
        fun getIntent(context: Context, account: GoogleSignInAccount): Intent {
            val intent = Intent(context, GoogleActivity::class.java)
            val bundle = Bundle().apply {
                putParcelable(GoogleLoginActivity.EXTRAS_ACCOUNT, account)
            }
            return intent.putExtras(bundle)
        }
    }
}
