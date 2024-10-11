package com.example.sportsstore

import android.app.AlertDialog
import android.content.Context

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatEditText
import com.example.sportsstore.databinding.FragmentAccountSettingBinding

class AccountSettingsFragment : Fragment() {
    private lateinit var binding: FragmentAccountSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Intialize dark mode
        val sharedPref = requireActivity().getSharedPreferences("AppSettings", 0)
        val isDarkModeOn = sharedPref.getBoolean("DarkMode", false)

        // Set initial switch state
        binding.DarkMode.isChecked = isDarkModeOn

        // Set the dark mode based on the saved preference
        setDarkMode(isDarkModeOn)

        // Toggle dark mode on switch change
        binding.DarkMode.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
            // Save user preference
            sharedPref.edit().putBoolean("DarkMode", isChecked).apply()

        }

        binding.btnChangeUsername.setOnClickListener {
            showChangeDialog("Change Username", "Enter new username:", "username")
        }

        binding.btnChangePassword.setOnClickListener {
            showChangeDialog("Change Password", "Enter new password:", "password")
        }
    }

    // Set dark mode based on the switch state
    private fun setDarkMode(isDarkModeOn: Boolean) {
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun showChangeDialog(title: String, message: String, type: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)

        val input = AppCompatEditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val newValue = input.text.toString()
            if (newValue.isNotEmpty()) {
                saveSetting(type, newValue)
                Toast.makeText(requireContext(), "$title updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveSetting(key: String, value: String) {
        val sharedPref = requireActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }
}
