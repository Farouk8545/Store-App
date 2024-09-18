package com.example.sportsstore.fragments// ResultProfileActivity.kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentProfileBinding

class FragmentProfileBinding : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.settingButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentProfileBinding_to_fragmentSettingBinding)
        }

        return binding.root
    }
}