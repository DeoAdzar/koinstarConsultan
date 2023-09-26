package com.trial.koinstar.consultan.v2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.trial.koinstar.consultan.v2.R
import com.trial.koinstar.consultan.v2.databinding.ActivityOnBoardBinding

private lateinit var binding: ActivityOnBoardBinding
class OnBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_primary)
        val flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flag
        binding = ActivityOnBoardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.onBoardStart.setOnClickListener {
            startActivity(Intent(applicationContext,LoginActivity::class.java))
            finish()
        }    }
}