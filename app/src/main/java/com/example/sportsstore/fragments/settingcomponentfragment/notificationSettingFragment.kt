package com.example.sportsstore

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment

class NotificationSettingsFragment : Fragment(R.layout.fragment_notification_setting) {

    private lateinit var pushNotificationsSwitch: Switch
    private lateinit var emailNotificationsSwitch: Switch
    private lateinit var vibrationSwitch: Switch

    private val CHANNEL_ID = "purchase_notification_channel"
    private val NOTIFICATION_ID = 1
    private val REQUEST_CODE_POST_NOTIFICATIONS = 101

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize switches
        pushNotificationsSwitch = view.findViewById(R.id.switch_push_notifications)
        emailNotificationsSwitch = view.findViewById(R.id.switch_email_notifications)
        vibrationSwitch = view.findViewById(R.id.switch_vibration)

        // Check and request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }

        createNotificationChannel()

        // Load saved settings from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        pushNotificationsSwitch.isChecked = sharedPref.getBoolean("push_notifications", true)
        emailNotificationsSwitch.isChecked = sharedPref.getBoolean("email_notifications", true)
        vibrationSwitch.isChecked = sharedPref.getBoolean("vibration", true)

        // Set listeners for the switches
        pushNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("push_notifications", isChecked)
            Toast.makeText(requireContext(), getString(R.string.push_notifications) + " ${if (isChecked) getString(R.string.enabled) else getString(R.string.disabled)}", Toast.LENGTH_SHORT).show()
        }

        emailNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("email_notifications", isChecked)
            Toast.makeText(requireContext(), getString(R.string.email_notifications) + " ${if (isChecked) getString(R.string.enabled) else getString(R.string.disabled)}", Toast.LENGTH_SHORT).show()
        }

        vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("vibration", isChecked)
            Toast.makeText(requireContext(), getString(R.string.vibration) + " ${if (isChecked) getString(R.string.enabled) else getString(R.string.disabled)}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSetting(key: String, value: Boolean) {
        val sharedPref = requireActivity().getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Purchase Notifications"
            val descriptionText = "Channel for purchase notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
