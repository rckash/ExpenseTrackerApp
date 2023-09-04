package com.example.expensetrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import com.example.expensetrackerapp.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide App Title in Action Bar
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Hide Progress Bar
        binding.progressBarRegistration.isVisible = false

        binding.btnRegister.setOnClickListener {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.text = ""
            binding.progressBarRegistration.isVisible = true

            var email = binding.tfEmailRegistration.editText?.text.toString()
            var password = binding.tfPasswordRegistration.editText?.text.toString()



            if (email.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Email and/or Password is empty", Toast.LENGTH_SHORT,).show()
            } else if (password.isNullOrEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Email and/or Password is empty",
                    Toast.LENGTH_SHORT,
                ).show()
            } else if (password.length < 6) {
                    Toast.makeText(applicationContext, "Password must be 6 characters or more", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("RegistrationActivity", "createUserWithEmail:success")
                            Toast.makeText(applicationContext, "Registered successfully.", Toast.LENGTH_SHORT,).show()
                            val user = auth.currentUser

                            val goToMainActivityIntent = Intent(this@RegistrationActivity, MainActivity::class.java)
                            startActivity(goToMainActivityIntent)
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("RegistrationActivity", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(applicationContext, "Invalid Email and/or Password", Toast.LENGTH_SHORT,).show()
                        }
                    }

            }
            binding.btnRegister.isEnabled = true
            binding.btnRegister.text = "Register"
            binding.progressBarRegistration.isVisible = false
        }

        binding.tvLogin.setOnClickListener {
            val goToLoginActivityIntent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(goToLoginActivityIntent)
            finish()
        }


    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val goToMainActivityIntent = Intent(this@RegistrationActivity, MainActivity::class.java)
            startActivity(goToMainActivityIntent)
            finish()
        }
    }
}