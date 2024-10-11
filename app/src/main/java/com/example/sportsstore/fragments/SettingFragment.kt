package com.example.sportsstore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentSettingBinding

class FragmentSettingBinding : Fragment() {

    // ViewBinding variable
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using ViewBinding
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.accountSettingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentSettingBinding_to_accountSettingsFragment)
        }

        binding.notificationSettingsButton.setOnClickListener {
            // Handle Notification Settings button click
            findNavController().navigate(R.id.action_fragmentSettingBinding_to_notificationSettingsFragment)
        }

        binding.privacySettingsButton.setOnClickListener {
            // Handle Privacy & Security button click
            findNavController().navigate(R.id.action_fragmentSettingBinding_to_privacySecuritySettingsFragment)
        }

        binding.paymentSettingsButton.setOnClickListener {
            // Handle Payment Methods button click
        }

        binding.languageSettingsButton.setOnClickListener {
            // Handle Language Selection button click
            findNavController().navigate(R.id.action_fragmentSettingBinding_to_languageSettingsFragment)
        }

        binding.helpSettingsButton.setOnClickListener {
            // Handle Help & Support button click
            findNavController().navigate(R.id.action_fragmentSettingBinding_to_helpSupportFragment)
        }
        return binding.root
    }
}
