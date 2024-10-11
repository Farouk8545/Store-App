package com.example.sportsstore.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentAdminManageCustomerBinding
import com.google.firebase.firestore.FirebaseFirestore


class AdminManageCustomerFragment : Fragment() {
    lateinit var binding : FragmentAdminManageCustomerBinding
    lateinit var firestore : FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminManageCustomerBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        firestore = FirebaseFirestore.getInstance()

        binding.button.setOnClickListener{
            var adminEmail = binding.editTextText.text.toString()
            if(adminEmail.isNotEmpty()){
                creatNewAdmin(adminEmail)
            }else{
                binding.editTextText.setHint("Enter an email")
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    fun creatNewAdmin(adminEmail :String){

    }


}