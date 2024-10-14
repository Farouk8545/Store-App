package com.example.sportsstore

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sportsstore.databinding.ActivitySplashScreenBinding
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[AuthViewModel::class.java]

        lifecycleScope.launch {
            // Apply a delay before proceeding
            delay(1000)

            // After the delay, observe the user LiveData
            authViewModel.user.observe(this@SplashScreenActivity) { user ->
                if (user != null) {
                    authViewModel.user.value?.uid?.let {
                        FirebaseFirestore.getInstance().collection("users").document(it)
                    }?.get()?.addOnSuccessListener { document ->
                        if (document.exists()) {
                            val role = document.getString("role")
                            when (role) {
                                "admin" -> {
                                    val intent = Intent(this@SplashScreenActivity, AdminDashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                "customer" -> {
                                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }else{
                            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    val intent = Intent(this@SplashScreenActivity, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}