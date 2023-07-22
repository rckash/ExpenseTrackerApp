package com.example.expensetrackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.expensetrackerapp.databinding.ActivityMainBinding
import com.example.expensetrackerapp.databinding.FragmentGoalsBinding
import com.example.expensetrackerapp.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fragments Instantiation
        val HomeFragment = HomeFragment()
        val ReportFragment = ReportFragment()
        val GoalsFragment = GoalsFragment()

        // Default Fragment Setting
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, HomeFragment)
            commit()
        }

        // Fragments Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_home -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, HomeFragment)
                        commit()
                    }
                }
                R.id.item_report -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, ReportFragment)
                        commit()
                    }
                }
                R.id.item_goals -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, GoalsFragment)
                        commit()
                    }
                }
            }
            true
        }

    }
}