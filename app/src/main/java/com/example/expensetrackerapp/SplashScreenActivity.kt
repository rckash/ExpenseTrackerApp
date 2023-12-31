package com.example.expensetrackerapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.expensetrackerapp.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val SPLASH_DELAY: Long = 2000 // milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // lock screen orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        CoroutineScope(Dispatchers.Main).launch {
            delay(SPLASH_DELAY)
            val myIntent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            startActivity(myIntent)
            finish()
        }


        // Hide App Title in Action Bar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()


    }

}