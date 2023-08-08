package com.example.expensetrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.btnLogin.setOnClickListener {
            var email = binding.tfEmailLogin.text.toString()
            var password = binding.tfPasswordLogin.text.toString()

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
                        Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                    }
                }

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