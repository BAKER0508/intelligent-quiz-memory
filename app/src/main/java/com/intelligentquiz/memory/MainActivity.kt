package com.intelligentquiz.memory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.intelligentquiz.memory.databinding.ActivityMainBinding
import com.intelligentquiz.memory.fragments.HomeFragment
import com.intelligentquiz.memory.fragments.LibraryFragment
import com.intelligentquiz.memory.fragments.StatisticsFragment
import com.intelligentquiz.memory.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when(item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_library -> LibraryFragment()
                R.id.nav_statistics -> StatisticsFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            
            true
        }
    }
}