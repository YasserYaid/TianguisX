package com.universidadveracruzana.tianguisx.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.universidadveracruzana.tianguisx.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showSplash()
    }

    private fun showSplash(){
        object : CountDownTimer(6000,1000){
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = "seconds remaining: " + millisUntilFinished / 1000
            }

            override fun onFinish() {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }

}