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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private lateinit var binding: ActivityMainBinding
lateinit var authViewModel: AuthViewModel
private lateinit var navViewModel: NavViewModel
class MainActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLocale(newBase)))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPref.getBoolean("DarkMode", false)

        // Apply the dark mode setting before setting the content view
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //livedate
        navViewModel = ViewModelProvider(this)[NavViewModel::class.java]

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

        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.search -> {
                    navController.navigate(R.id.searchFragment2)
                    true
                }
                R.id.profile -> {
                    navController.navigate(R.id.fragmentProfileBinding)
                    true
                }
                R.id.favorite -> {
                    navController.navigate(R.id.favoriteFragment)
                    true
                }
                R.id.purchase_cart -> {
                    navController.navigate(R.id.purchaseCartFragment2)
                    true
                }

                else -> false
            }
        }

        authViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[AuthViewModel::class.java]
        authViewModel.initGoogleSignInClient(this)
        authViewModel.user.observe(this){ user ->
            if(user == null){
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        GlobalScope.launch {
            if(!checkForNewUser()) {
                withContext(Dispatchers.Main){
                    navController.navigate(R.id.signUpWithGoogleFragment)
                }
            }
        }
    }

    /*
    * File:   MainActivity
    * Author: mokhtar
    * Created on september 15, 2024, 11:26 AM
    */
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private suspend fun checkForNewUser(): Boolean{
        val firestore = FirebaseFirestore.getInstance()
        val userRef = authViewModel.user.value?.let {
            firestore.collection("users").document(it.uid)
        }

        return try {
            val documentSnapshot = userRef?.get()?.await()
            documentSnapshot?.exists() ?: false
        }catch (e: Exception){
            false
        }
    }
}