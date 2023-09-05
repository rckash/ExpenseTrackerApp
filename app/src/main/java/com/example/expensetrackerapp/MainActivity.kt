package com.example.expensetrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.expensetrackerapp.databinding.ActivityMainBinding
import com.example.expensetrackerapp.fragments.GoalsFragment
import com.example.expensetrackerapp.fragments.HomeFragment
import com.example.expensetrackerapp.fragments.ReportFragment
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var appDB: AppDatabase

    lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide App Title in Action Bar
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // database instantiation
        appDB = AppDatabase.invoke(applicationContext)

        // Fragments Instantiation
        val HomeFragment = HomeFragment()
        val ReportFragment = ReportFragment()
        val GoalsFragment = GoalsFragment()

        // Default Fragment Setting
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, HomeFragment)
            commit()
        }

        // nav drawer setup
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        drawerToggle = ActionBarDrawerToggle( this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // fragments navigation
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {

                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, HomeFragment)
                        commit()
                    }
                    drawerLayout.close()

                }
                R.id.nav_report -> {

                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, ReportFragment)
                        commit()
                    }
                    drawerLayout.close()

                }
                R.id.nav_goals -> {

                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, GoalsFragment)
                        commit()
                    }
                    drawerLayout.close()

                }
                R.id.sync -> {

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
                R.id.sign_out -> {
                    auth.signOut()

                    val goToLoginActivityIntent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(goToLoginActivityIntent)
                    finish()
                }
            }
            true
        }

    }

    private fun uploadToFireStore() {

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val goToMainActivityIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(goToMainActivityIntent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}