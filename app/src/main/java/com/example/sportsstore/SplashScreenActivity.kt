package com.example.sportsstore

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sportsstore.databinding.ActivitySplashScreenBinding
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_screen_video)

        binding.videoView.setVideoURI(videoUri)

        binding.videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start() // Start the video when it's ready
        }

        authViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[AuthViewModel::class.java]

        binding.videoView.setOnCompletionListener {
            authViewModel.user.observe(this){ user ->
                if(user != null){
                    authViewModel.user.value?.uid?.let {
                        FirebaseFirestore.getInstance().collection("users").document(it)
                    }?.get()?.addOnSuccessListener { document ->
                        if(document.exists()){
                            val role = document.getString("role")
                            when (role) {
                                "admin" -> {
                                    val intent = Intent(this, AdminDashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                "customer" -> {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else -> {
                                    val intent = Intent(this, SignInActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}