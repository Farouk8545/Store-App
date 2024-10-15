package com.example.sportsstore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sportsstore.databinding.ActivityMainBinding
import com.example.sportsstore.viewmodels.AuthViewModel
import com.example.sportsstore.viewmodels.NavViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private lateinit var binding: ActivityMainBinding
private lateinit var authViewModel: AuthViewModel
private lateinit var navViewModel: NavViewModel

class MainActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLocale(newBase)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in binding layout: ${e.message}")
            e.printStackTrace()
            return
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        // Load dark mode preference
        val sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkModeOn = sharedPref.getBoolean("DarkMode", false)
        AppCompatDelegate.setDefaultNightMode(if (isDarkModeOn) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)

        navViewModel = ViewModelProvider(this)[NavViewModel::class.java]
        if (navController == null) {
            Log.e("MainActivity", "Navigation controller not found")
            return
        }
        // Listen to fragment changes and update the ViewModel
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("MainActivity", "Navigated to destination: ${destination.id}")
            navViewModel.setCurrentFragment(destination.id)
        }
        // Observe current fragment LiveData and update the bottom navigation
        navViewModel.currentFragmentId.observe(this) { fragmentId ->
            Log.d("MainActivity", "Current fragment ID: $fragmentId")
            binding.bottomNavigationView.selectedItemId = when (fragmentId) {
                R.id.homeFragment -> R.id.home
                R.id.searchFragment2 -> R.id.search
                R.id.fragmentProfileBinding -> R.id.profile
                R.id.favoriteFragment -> R.id.favorite
                R.id.purchaseCartFragment2 -> R.id.purchase_cart
                else -> {
                    Log.d("MainActivity", "Unknown fragment ID: $fragmentId")
                    return@observe // Prevent selecting an unknown item
                }
            }
        }

        // Handle bottom navigation item selection
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> navController.navigate(R.id.homeFragment)
                R.id.search -> navController.navigate(R.id.searchFragment2)
                R.id.profile -> navController.navigate(R.id.fragmentProfileBinding)
                R.id.favorite -> navController.navigate(R.id.favoriteFragment)
                R.id.purchase_cart -> navController.navigate(R.id.purchaseCartFragment2)
                else -> false
            }
            true
        }

        // Initialize AuthViewModel and check user authentication
        authViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[AuthViewModel::class.java]
        authViewModel.initGoogleSignInClient(this)
        authViewModel.user.observe(this) { user ->
            if (user == null) {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Launch check for new user
        coroutineScope.launch {
            if (!checkForNewUser()) {
                navController.navigate(R.id.signUpWithGoogleFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private suspend fun checkForNewUser(): Boolean {
        val firestore = FirebaseFirestore.getInstance()
        val userRef = authViewModel.user.value?.let { firestore.collection("users").document(it.uid) }

        return try {
            val documentSnapshot = userRef?.get()?.await()
            documentSnapshot?.exists() ?: false
        } catch (e: Exception) {
            Log.e("MainActivity", "Error checking for new user: ${e.message}")
            false
        }
    }
