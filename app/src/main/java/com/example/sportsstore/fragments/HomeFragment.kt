package com.example.sportsstore.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.SignInActivity
import com.example.sportsstore.adapters.ParentAdapter
import com.example.sportsstore.databinding.FragmentHomeBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.models.ParentItem
import com.example.sportsstore.viewmodels.AuthViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var authViewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val shirtsList: List<ChildItem> = listOf(
            ChildItem("Real Madrid - Home", "2024", 150.0,
                R.mipmap.real_madrid_home_2024_foreground
            ),
            ChildItem("Real Madrid - Home", "2023", 150.0,
                R.mipmap.real_madrid_home_2023_foreground
            ),
            ChildItem("Real Madrid - Away", "2023", 150.0,
                R.mipmap.real_madrid_away_2023_foreground
            )
        )

        val shoesList: List<ChildItem> = listOf(
            ChildItem("Nike", null, 350.0, R.mipmap.nike_shoes_foreground),
            ChildItem("Adidas", null, 400.0, R.mipmap.adidas_shoes_foreground),
            ChildItem("Puma", null, 300.0, R.mipmap.puma_shoes_foreground)
        )

        val latestsList: List<ChildItem> = listOf(
            ChildItem("Sweat Pants", null, 100.0, R.mipmap.sweatpants_foreground),
            ChildItem("Socks", null, 50.0, R.mipmap.socks_foreground),
            ChildItem("Hat", null, 120.0, R.mipmap.hat_foreground)
        )

        val recyclerView = binding.verticalRv
        val adapter = ParentAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setData(listOf(
            ParentItem("Clubs Shirts", shirtsList),
            ParentItem("Shoes", shoesList),
            ParentItem("Latests", latestsList)
            ))

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]

        authViewModel.initGoogleSignInClient(requireContext())

        binding.userImage.setOnClickListener {
            authViewModel.signOut()
        }

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            if(user != null){
                binding.userName.text = user.displayName
                binding.userImage.setImageURI(user.photoUrl)
            }else {
                val intent = Intent(requireContext(), SignInActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        return binding.root
    }
}