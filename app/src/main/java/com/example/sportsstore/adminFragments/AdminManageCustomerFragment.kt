package com.example.sportsstore.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
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
        binding.imageView6.setOnClickListener{
            findNavController().navigate(R.id.action_adminManageCustomerFragment_to_adminMainPageFragment)
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    fun creatNewAdmin(adminEmail :String){
        firestore.collection("users")
            .whereEqualTo("email",adminEmail)
            .get()
            .addOnSuccessListener { result ->
                if(result.documents.isNotEmpty()){
                    val document = result.documents[0].id

                    val updatedProduct = hashMapOf(
                        "role" to "admin")
                    firestore.collection("users")
                        .document(document)
                        .update(updatedProduct as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(context, "new admin add successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to add admin : ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }else{
                    Toast.makeText(context, "No email found with this context", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(context, "Error fetching email: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


}