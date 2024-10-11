package com.example.sportsstore.fragments// ResultProfileActivity.kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sportsstore.R
import com.example.sportsstore.SignInActivity
import com.example.sportsstore.databinding.FragmentProfileBinding
import com.example.sportsstore.viewmodels.AuthViewModel

class FragmentProfileBinding : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var authViewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.settingButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentProfileBinding_to_fragmentSettingBinding)
        }

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        context?.let { authViewModel.initGoogleSignInClient(it) }

        binding.logOutButton.setOnClickListener {
            authViewModel.signOut()
            val intent = Intent(requireActivity(), SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.orderHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentProfileBinding_to_purchaseHistoryFragment)
        }

        return binding.root
    }
}