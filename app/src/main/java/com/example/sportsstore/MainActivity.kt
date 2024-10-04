package com.example.sportsstore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sportsstore.databinding.ActivityMainBinding
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private lateinit var binding: ActivityMainBinding
private lateinit var authViewModel: AuthViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

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