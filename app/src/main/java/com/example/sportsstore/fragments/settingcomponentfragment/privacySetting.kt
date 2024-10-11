package com.example.sportsstore.fragments.settingcomponentfragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatEditText
import com.example.sportsstore.R

class PrivacySecuritySettingsFragment : Fragment(R.layout.fragment_privacy_setting) {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var twoFASwitch: Switch
    private lateinit var manageDevicesButton: Button
    private lateinit var clearDataButton: Button
    private lateinit var changeSecurityQuestionsButton: Button

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        twoFASwitch = view.findViewById(R.id.switch_two_fa)
        clearDataButton = view.findViewById(R.id.btn_clear_data)
        changeSecurityQuestionsButton = view.findViewById(R.id.btn_change_security_questions)

        // Load saved settings
        val sharedPref = requireActivity().getSharedPreferences("privacy_security_settings", Context.MODE_PRIVATE)
        twoFASwitch.isChecked = sharedPref.getBoolean("two_fa", false)

        twoFASwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("two_fa", isChecked)
            Toast.makeText(requireContext(), "Two-Factor Authentication ${if (isChecked) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }


        clearDataButton.setOnClickListener {
            showClearDataConfirmation()
        }

        changeSecurityQuestionsButton.setOnClickListener {
            showChangeSecurityQuestionsDialog()
        }
    }

    private fun showClearDataConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear App Data")
            .setMessage("Are you sure you want to clear all app data?")
            .setPositiveButton("Yes",) { dialog, _ ->
                // Implement data clearing logic
                // faruok make data clear
                Toast.makeText(requireContext(), "App data cleared", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showChangeSecurityQuestionsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Security Questions")
        builder.setMessage("Enter your new security question:")

        val input = AppCompatEditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val newQuestion = input.text.toString()
            if (newQuestion.isNotEmpty()) {
                saveSetting("security_question", newQuestion)
                Toast.makeText(requireContext(), "Security Question updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveSetting(key: String, value: Any) {
        val sharedPref = requireActivity().getSharedPreferences("privacy_security_settings", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
            }
            apply()
        }
    }
}
