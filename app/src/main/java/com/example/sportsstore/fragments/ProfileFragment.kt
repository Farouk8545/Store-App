package com.example.sportsstore.fragments// ResultProfileActivity.kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.sportsstore.R
import com.example.sportsstore.SignInActivity
import com.example.sportsstore.databinding.FragmentProfileBinding
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

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

        binding.userName.text = if(authViewModel.user.value?.displayName.isNullOrEmpty()) authViewModel.user.value?.email?.substringBefore("@") ?: "User" else authViewModel.user.value?.displayName
        authViewModel.user.value?.photoUrl?.let { uri ->
            Glide.with(binding.profilePic)
                .load(uri)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.baseline_account_circle_24)
                .into(binding.profilePic)
        } ?: run {
            // Set default image if the photo URL is null
            binding.profilePic.setImageResource(R.drawable.baseline_account_circle_24)
        }

        val doc = authViewModel.user.value?.uid?.let {
            FirebaseFirestore.getInstance().collection("users").document(it)
        }
        doc?.get()?.addOnSuccessListener { document ->
            if(document != null && document.exists()){
                val address = document.getString("address")
                val phoneNumber = document.getString("phoneNumber")

                binding.address.text = address
                binding.phoneNumber.text = phoneNumber
            }
        }

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