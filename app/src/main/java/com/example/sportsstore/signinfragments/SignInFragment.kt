package com.example.sportsstore.signinfragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sportsstore.AdminDashboardActivity
import com.example.sportsstore.MainActivity
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentSignInBinding
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SignInFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: FragmentSignInBinding

    // Register the activity result launcher
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the Google Sign-In result
            authViewModel.handleSignInResult(result.data)
        } else {
            Toast.makeText(requireContext(), "Sign in failed!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view model
        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]

        // Initialize Google Sign-In client
        authViewModel.initGoogleSignInClient(requireActivity())

        // Observe user data
        authViewModel.user.observe(viewLifecycleOwner){ user ->
            if(user != null){
                authViewModel.user.value?.uid?.let {
                    FirebaseFirestore.getInstance().collection("users").document(it)
                }?.get()?.addOnSuccessListener { document ->
                    if(document.exists()){
                        val role = document.getString("role")
                        if(role == "admin"){
                            val intent = Intent(requireActivity(), AdminDashboardActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }else if(role == "customer"){
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }
            }
        }

        // Set click listener for Google Sign-In button
        binding.googleSignInButton.setOnClickListener {
            authViewModel.signIn(signInLauncher)
        }

        // Set click listener for sign up text
        binding.signUpText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        // Set click listener for email sign in button
        binding.emailSignInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.signIn(email, password)
            }else Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
        }
    }
}