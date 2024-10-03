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
import com.example.sportsstore.databinding.FragmentSignUpBinding
import com.example.sportsstore.viewmodels.AuthViewModel

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            if(user != null){
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }

        binding.emailSignUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val phoneNumber = binding.phoneNumberEditText.text.toString()
            val address = binding.addressEditText.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty() && phoneNumber.isNotEmpty() && address.isNotEmpty()){
                authViewModel.createAccount(email, password, phoneNumber, address)
            }else Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
        }
    }
}