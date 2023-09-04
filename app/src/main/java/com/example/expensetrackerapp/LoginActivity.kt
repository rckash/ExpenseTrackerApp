package com.example.expensetrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.expensetrackerapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide App Title in Action Bar
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Hide Progress Bar
        binding.progressBarLogin.isVisible = false

        binding.btnLogin.setOnClickListener {
            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = ""
            binding.progressBarLogin.isVisible = true

            var email = binding.tfEmailLogin.editText?.text.toString()
            var password = binding.tfPasswordLogin.editText?.text.toString()

            if (email.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Email and/or Password is empty", Toast.LENGTH_SHORT,).show()
            } else if (password.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Email and/or Password is empty", Toast.LENGTH_SHORT,).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)

                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginActivity", "signInWithEmail:success")
                            val user = auth.currentUser
                            Toast.makeText(this, "Logged In Successfully", Toast.LENGTH_SHORT).show()

                            val goToMainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(goToMainActivityIntent)
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                            Toast.makeText(applicationContext, "Invalid Email and/or Password", Toast.LENGTH_SHORT,).show()
                        }
                    }
            }
            binding.btnLogin.isEnabled = true
            binding.btnLogin.text = "Login"
            binding.progressBarLogin.isVisible = false
        }

        binding.tvRegister.setOnClickListener {
            val goToRegistrationActivityIntent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(goToRegistrationActivityIntent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val goToMainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(goToMainActivityIntent)
            finish()
        }
    }
}