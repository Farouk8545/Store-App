package com.example.sportsstore

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.sportsstore.databinding.ActivityMainBinding
import com.example.sportsstore.databinding.FragmentSearchBinding
import com.example.sportsstore.search.SearchFragment

lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

    }

    /*
    * File:   MainActivity
    * Author: mokhtar
    * Created on september 15, 2024, 11:26 AM
    */
    override fun onSupportNavigateUp(): Boolean {
        val navhostfrafment = supportFragmentManager.findFragmentById(R.id.searchFragment) as NavHostFragment
        val navConttroller = navhostfrafment.navController
        return navConttroller.navigateUp() || super.onSupportNavigateUp()
    }
}