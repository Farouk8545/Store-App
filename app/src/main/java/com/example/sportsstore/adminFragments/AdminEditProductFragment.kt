package com.example.sportsstore.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentAdminEditProductBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminEditProductFragment : Fragment() {
    lateinit var binding : FragmentAdminEditProductBinding
    lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminEditProductBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        firestore = FirebaseFirestore.getInstance()

        binding.searchById.setOnClickListener{
            var productId = binding.idEt.text.toString()
            if(productId.isNotEmpty()){
                loadProductById(productId)
            }else{
                binding.idEt.setText("please enter ID")
            }
        }


        // Inflate the layout for this fragment
        return binding.root

    }

    fun loadProductById(productId: String){
        firestore.collection("sports_shirts")
            .whereEqualTo("id",productId)
            .get()
            .addOnSuccessListener { result ->
                if(result.documents.isNotEmpty()){
                    var document = result.documents[0]
                    var documentId = document.id

                    binding.nameEt.setText(document.getString("productName"))
                    binding.descriptionEt.setText(document.getString("description"))
                    binding.priceEt.setText(document.getString("price"))
                    binding.idEt.setText(document.getString("id"))
                    binding.yearEt.setText(document.getString("year"))

                }
            }
    }


}