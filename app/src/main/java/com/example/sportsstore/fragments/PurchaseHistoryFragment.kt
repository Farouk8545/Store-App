package com.example.sportsstore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.PurchaseHistoryAdapter
import com.example.sportsstore.databinding.FragmentPurchaseHistoryBinding
import com.example.sportsstore.models.PurchaseModel
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PurchaseHistoryFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel

    private lateinit var binding: FragmentPurchaseHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPurchaseHistoryBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setPadding(0, 0, 0, 0)
        toolbar.setContentInsetsAbsolute(0, 0)

        val layoutParams = androidx.appcompat.widget.Toolbar.LayoutParams(
            androidx.appcompat.widget.Toolbar.LayoutParams.MATCH_PARENT,
            androidx.appcompat.widget.Toolbar.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = android.view.Gravity.CENTER  // Center the custom view in the Toolbar

        val customToolbar = LayoutInflater.from(requireContext()).inflate(R.layout.custom_toolbar_layout, toolbar, false)
        toolbar.addView(customToolbar, layoutParams)

        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]
        val adapter = PurchaseHistoryAdapter(authViewModel)
        binding.purchaseHistoryRv.adapter = adapter
        binding.purchaseHistoryRv.layoutManager = LinearLayoutManager(requireContext())

        FirebaseFirestore.getInstance().collection("users").document(authViewModel.user.value?.uid ?: "").collection("purchases")
            .get().addOnSuccessListener {
                adapter.setData(it.toObjects(PurchaseModel::class.java))
            }

        return binding.root
    }
}