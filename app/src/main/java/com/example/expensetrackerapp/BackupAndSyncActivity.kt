package com.example.expensetrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.expensetrackerapp.databinding.ActivityBackupAndSyncBinding
import com.example.expensetrackerapp.databinding.ActivityMainBinding
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BackupAndSyncActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBackupAndSyncBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var appDB: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBackupAndSyncBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        appDB = AppDatabase.invoke(applicationContext)

        binding.btnUpload.setOnClickListener {

            Upload()

        }

    }

    private fun Upload() {

        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()
        val db = Firebase.firestore

        GlobalScope.launch(Dispatchers.IO) {
            val expenses = db.collection("$userUid")

            for (expense in appDB.getExpenses().getAllExpenses()) {
                val city = hashMapOf(
                    "name" to expense.name,
                    "price" to expense.price,
                    "category" to expense.category,
                    "dateInt" to expense.dateInt,
                    "dateString" to expense.dateString,
                    "isExpense" to expense.isExpense
                )

                // Add a new document with a generated ID
                expenses.add(city)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(
                            applicationContext,
                            "Uploaded to Firebase",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            applicationContext,
                            "Failed to upload to Online Database",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.w("TAG", "Error adding document", e)
                    }
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Synced", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val goToMainActivityIntent = Intent(this@BackupAndSyncActivity, LoginActivity::class.java)
            startActivity(goToMainActivityIntent)
            finish()
        }
    }

}