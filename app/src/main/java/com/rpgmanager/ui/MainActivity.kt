package com.rpgmanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.rpgmanager.R
import com.rpgmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Top-level destinations (no back arrow)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.groupListFragment),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)

        // Hide bottom nav on detail screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideOnDestinations = setOf(
                R.id.groupFormFragment,
                R.id.characterFormFragment,
                R.id.attributeFormFragment,
                R.id.characterDetailFragment
            )
            binding.bottomNav.visibility =
                if (destination.id in hideOnDestinations)
                    android.view.View.GONE
                else
                    android.view.View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
