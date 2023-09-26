package com.trial.koinstar.consultan.v2.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.trial.koinstar.consultan.v2.R
import com.trial.koinstar.v2.utils.SharedPreferencesManager

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var session: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_splash_screen)
        session = SharedPreferencesManager(applicationContext)

        Handler().postDelayed({
            session.checkLogin()
            finish()
        },2000)
    }
}