package com.example.expensetrackerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.expensetrackerapp.databinding.ActivityMainBinding
import com.example.expensetrackerapp.fragments.GoalsFragment
import com.example.expensetrackerapp.fragments.HomeFragment
import com.example.expensetrackerapp.fragments.ReportFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide App Title in Action Bar
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        // Initialize Firebase Auth
        auth = Firebase.auth

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