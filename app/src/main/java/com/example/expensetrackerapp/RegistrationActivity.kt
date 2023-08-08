package com.example.expensetrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.btnRegister.setOnClickListener {
            var email = binding.tfEmailRegistration.text.toString()
            var password = binding.tfPasswordRegistration.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("RegistrationActivity", "createUserWithEmail:success")
                        val user = auth.currentUser

                        val goToMainActivityIntent = Intent(this@RegistrationActivity, MainActivity::class.java)
                        startActivity(goToMainActivityIntent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("RegistrationActivity", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                    }
                }

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