package com.example.sportsstore.fragments// ResultProfileActivity.kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentProfileBinding
import com.example.sportsstore.models.User
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FragmentProfileBinding : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.settingButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentProfileBinding_to_fragmentSettingBinding)
        }

        binding.orderHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentProfileBinding_to_purchaseHistoryFragment)
        }

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]

        authViewModel.user.observe(viewLifecycleOwner){user ->
            if(user != null){
                user.photoUrl?.let {
                    binding.profilePic.setImageURI(it)
                } ?: run {
                    // Set default image if the photo URL is null
                    binding.profilePic.setImageResource(R.drawable.baseline_account_circle_24)
                }
            }
        }

        GlobalScope.launch {
            val userInformationDeferred = async {
                val firestore = FirebaseFirestore.getInstance()
                val userRef = authViewModel.user.value?.let {
                    firestore.collection("users").document(it.uid)
                }
                userRef?.get()?.await()?.toObject(User::class.java)
            }

            val userInformation = userInformationDeferred.await()

            withContext(Dispatchers.Main){
                binding.userName.text = userInformation?.displayName ?: "User"
                binding.phoneNumber.text = userInformation?.phoneNumber ?: "Phone Number"
                binding.address.text = userInformation?.address ?: "Address"
            }
        }

        binding.logOutButton.setOnClickListener {
            authViewModel.signOut()
        }

        return binding.root
    }
}