package com.example.expensetrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        // Hide App Title in Action Bar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()

        CoroutineScope(Dispatchers.Main).launch {
            delay(SPLASH_DELAY)
            val myIntent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            startActivity(myIntent)
            finish()
        }
    }
}