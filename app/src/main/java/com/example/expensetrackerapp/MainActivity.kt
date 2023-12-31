package com.example.expensetrackerapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.expensetrackerapp.databinding.ActivityMainBinding
import com.example.expensetrackerapp.fragments.BackupAndSyncFragment
import com.example.expensetrackerapp.fragments.GoalsFragment
import com.example.expensetrackerapp.fragments.HomeFragment
import com.example.expensetrackerapp.fragments.ReportFragment
import com.example.expensetrackerapp.fragments.TutorialFragment
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var appDB: AppDatabase

    private lateinit var sharedPreferences: SharedPreferences

    lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // lock screen orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        sharedPreferences = getSharedPreferences("FirstUsePreference", MODE_PRIVATE)

        // Fragments Instantiation
        val HomeFragment = HomeFragment()
        val ReportFragment = ReportFragment()
        val GoalsFragment = GoalsFragment()
        val BackupAndSyncFragment = BackupAndSyncFragment()
        val TutorialFragment = TutorialFragment()


        //get shared pref
        val isFirstUse = sharedPreferences.getBoolean("isFirstUse", true)

        if (isFirstUse) {

            val editor = sharedPreferences.edit()
            editor.putBoolean("isFirstUse", true)
            editor.apply()
            Log.d("SplashScreen", "Shared Preference Edited")

            // Default Fragment Setting
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, TutorialFragment)
                commit()
            }

        } else {

            // Default Fragment Setting
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, HomeFragment)
                commit()
            }

        }

        // Hide App Title in Action Bar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // database instantiation
        appDB = AppDatabase.invoke(applicationContext)




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

                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isFirstUse", false)
                    editor.apply()
                    Log.d("SplashScreen", "Shared Preference Edited")

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

                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, BackupAndSyncFragment)
                        commit()
                    }
                    drawerLayout.close()

                }
                R.id.tutorial -> {

                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, TutorialFragment)
                        commit()
                    }
                    drawerLayout.close()

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