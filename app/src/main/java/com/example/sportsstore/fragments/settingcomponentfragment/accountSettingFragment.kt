package com.example.sportsstore

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
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

        // Initialize dark mode based on saved preference
        val sharedPref = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPref.getBoolean("DarkMode", false)

        // Set the initial switch state based on the saved preference
        binding.DarkMode.isChecked = isDarkModeOn

        // Apply the dark mode setting immediately on fragment creation
        setDarkMode(isDarkModeOn)

        // Toggle dark mode and save the preference when the switch is toggled
        binding.DarkMode.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
            // Save user preference for dark mode
            sharedPref.edit().putBoolean("DarkMode", isChecked).apply()
        }

        // Set up other UI elements (Change Username and Password buttons)
        binding.btnChangeUsername.setOnClickListener {
            showChangeDialog(getString(R.string.change_username),
                getString(R.string.enter_new_username), "username")
        }

        binding.btnChangePassword.setOnClickListener {
            showChangeDialog(getString(R.string.change_password),
                getString(R.string.enter_new_password), "password")
        }
    }

    // Function to apply dark mode based on the boolean parameter
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

        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            val newValue = input.text.toString()
            if (newValue.isNotEmpty()) {
                saveSetting(type, newValue)
                Toast.makeText(requireContext(), "$title updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.input_cannot_be_empty), Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveSetting(key: String, value: String) {
        val sharedPref = requireActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }
}
