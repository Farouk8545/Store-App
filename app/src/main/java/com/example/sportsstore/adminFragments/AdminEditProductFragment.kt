package com.example.sportsstore.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.AdminColorChoiceAdapter
import com.example.sportsstore.databinding.FragmentAdminEditProductBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminEditProductFragment : Fragment() {
    lateinit var binding : FragmentAdminEditProductBinding
    lateinit var firestore: FirebaseFirestore
    private var documentId: String? = null
    private lateinit var adapter: AdminColorChoiceAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminEditProductBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        firestore = FirebaseFirestore.getInstance()

        adapter = AdminColorChoiceAdapter()
        binding.colorsRv.adapter = adapter
        binding.colorsRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.searchById.setOnClickListener{
            val productId = binding.idInput.text.toString()
            if(productId.isNotEmpty()){
                loadProductById(productId)
            }else{
                binding.idInput.setText("please enter ID")
            }
        }

        binding.addProductButton.setOnClickListener {
            saveProductChanges()
        }

        binding.imageView6.setOnClickListener{
            findNavController().navigate(R.id.action_adminEditProductFragment_to_adminMainPageFragment)
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
                    val document = result.documents[0]
                    documentId = document.id


                    binding.nameEt.setText(document.getString("productName"))
                    binding.descriptionEt.setText(document.getString("description"))
                    binding.priceEt.setText(document.getDouble("price")?.toString())
                    binding.yearEt.setText(document.getString("year"))
                    document.get("colors")?.let { colors ->
                        if (colors is List<*>) {
                            // Check if the list is of type String
                            val colorList = colors.filterIsInstance<String>()
                            // Pass the list to the adapter
                            adapter.setData(colorList)
                        }
                    }
                    binding.layoutDisplay.visibility = View.VISIBLE


                }else{
                    binding.layoutDisplay.visibility = View.GONE
                    Toast.makeText(context, "No product found with this ID", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error fetching product: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
    fun saveProductChanges(){
        if (documentId != null) {
            // Get the updated details from EditText fields
            val updatedProduct = hashMapOf(
                "productName" to binding.nameEt.text.toString(),
                "price" to binding.priceEt.text.toString().toDouble(),
                "description" to binding.descriptionEt.text.toString(),
                "year" to binding.yearEt.text.toString()
                // Add other fields (colors, sizes, etc.) as needed
            )

            // Update the product in Firestore
            firestore.collection("sports_shirts")
                .document(documentId!!)
                .update(updatedProduct as Map<String, Any>)
                .addOnSuccessListener {
                    binding.layoutDisplay.visibility = View.GONE
                    Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                    binding.idInput.setText("")

                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update product: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No product loaded to update", Toast.LENGTH_SHORT).show()
        }
    }



}