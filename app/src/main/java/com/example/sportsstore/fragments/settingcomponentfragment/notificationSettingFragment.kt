package com.example.sportsstore

import android.content.Context
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment

class NotificationSettingsFragment : Fragment(R.layout.fragment_notification_setting) {

    private lateinit var pushNotificationsSwitch: Switch
    private lateinit var emailNotificationsSwitch: Switch
    private lateinit var vibrationSwitch: Switch

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pushNotificationsSwitch = view.findViewById(R.id.switch_push_notifications)
        emailNotificationsSwitch = view.findViewById(R.id.switch_email_notifications)
        vibrationSwitch = view.findViewById(R.id.switch_vibration)

        // Load saved settings
        val sharedPref = requireActivity().getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        pushNotificationsSwitch.isChecked = sharedPref.getBoolean("push_notifications", true)
        emailNotificationsSwitch.isChecked = sharedPref.getBoolean("email_notifications", true)
        vibrationSwitch.isChecked = sharedPref.getBoolean("vibration", true)

        pushNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("push_notifications", isChecked)
            Toast.makeText(requireContext(), getString(R.string.push_notifications) +" ${if (isChecked) getString(
                R.string.enabled
            ) else getString(R.string.disabled)}", Toast.LENGTH_SHORT).show()
        }

        emailNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("email_notifications", isChecked)
            Toast.makeText(requireContext(), getString(R.string.email_notifications) +" ${if (isChecked) getString(
                R.string.enabled
            ) else getString(R.string.disabled)}", Toast.LENGTH_SHORT).show()
        }

        vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("vibration", isChecked)
            Toast.makeText(requireContext(), getString(R.string.vibration) +" ${if (isChecked) getString(
                R.string.enabled
            ) else getString(R.string.disabled)}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSetting(key: String, value: Boolean) {
        val sharedPref = requireActivity().getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }
}
