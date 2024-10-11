package com.example.sportsstore.adminFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sportsstore.R
import com.example.sportsstore.SignInActivity
import com.example.sportsstore.databinding.FragmentAdminMainPageBinding
import com.example.sportsstore.viewmodels.AuthViewModel

class AdminMainPageFragment : Fragment() {
    private lateinit var binding: FragmentAdminMainPageBinding
    private lateinit var authViewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminMainPageBinding.inflate(inflater, container, false)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        context?.let { authViewModel.initGoogleSignInClient(it) }

        authViewModel.user.observe(viewLifecycleOwner) {
            if (it == null) {
                val intent = Intent(context, SignInActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }

        binding.addRemoveProductButton.setOnClickListener {
            findNavController().navigate(R.id.action_adminMainPageFragment_to_adminAddRemoveProductFragment)
        }
        binding.removeProductButton.setOnClickListener{
            findNavController().navigate(R.id.action_adminMainPageFragment_to_adminRemoveProductFragment2)
        }
        binding.editProductsButton.setOnClickListener{
            findNavController().navigate(R.id.action_adminMainPageFragment_to_adminEditProductFragment)
        }

        binding.logOutButton.setOnClickListener {
            authViewModel.signOut()
        }

        return binding.root
    }
}