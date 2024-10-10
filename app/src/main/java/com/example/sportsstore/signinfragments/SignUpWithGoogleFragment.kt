package com.example.sportsstore.signinfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentSignUpWithGoogleBinding
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SignUpWithGoogleFragment : Fragment() {
    private lateinit var binding: FragmentSignUpWithGoogleBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpWithGoogleBinding.inflate(layoutInflater, container, false)

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]

        binding.googleSignUpButton.setOnClickListener {
            val phoneNumber = binding.phoneNumberEditText.text.toString()
            val address = binding.addressEditText.text.toString()
            if(phoneNumber.isNotEmpty() && address.isNotEmpty()){
                val firestore = FirebaseFirestore.getInstance()
                val userRef = authViewModel.user.value?.let {
                    firestore.collection("users").document(it.uid)
                }

                val hashmap = hashMapOf(
                    "email" to authViewModel.user.value?.email,
                    "displayName" to authViewModel.user.value?.displayName,
                    "phoneNumber" to phoneNumber,
                    "address" to address,
                    "role" to "customer"
                )

                userRef?.set(hashmap)
                findNavController().navigate(R.id.action_signUpWithGoogleFragment_to_homeFragment)
            }else{
                Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

}