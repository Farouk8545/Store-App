package com.example.sportsstore.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentAdminRemoveProductBinding
import com.google.firebase.firestore.FirebaseFirestore


class AdminRemoveProductFragment : Fragment() {
    lateinit var binding :FragmentAdminRemoveProductBinding
    lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminRemoveProductBinding.inflate(inflater,container,false)
        firestore = FirebaseFirestore.getInstance()
        binding.lifecycleOwner = viewLifecycleOwner

        binding.button.setOnClickListener{
            val productId = binding.editTextText.text.toString()
            if(productId.isNotEmpty()){
                deleteProductById(productId)
            }else{
                binding.editTextText.setText("please enter ID")
            }
        }

        binding.imageView6.setOnClickListener{
            findNavController().navigate(R.id.action_adminRemoveProductFragment2_to_adminMainPageFragment)
        }

        // Inflate the layout for this fragment
        return binding.root

    }

    fun deleteProductById(productId: String) {
        // Delete the product from sports_shirts collection
        firestore.collection("sports_shirts")
            .whereEqualTo("id", productId)
            .get()
            .addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    val documentId = result.documents[0].id
                    firestore.collection("sports_shirts").document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                            binding.editTextText.text.clear()
                            // After deleting the product, delete it from all users' carts
                            deleteProductFromCarts(productId)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to delete product: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "No product found with this ID", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener {
                Toast.makeText(context, "Error fetching product: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteProductFromCarts(productId: String) {
        // Query all users' carts
        firestore.collection("users")
            .get()
            .addOnSuccessListener { users ->
                for (user in users.documents) {
                    val userId = user.id
                    val cartRef = firestore.collection("users").document(userId).collection("purchases_cart")
                    cartRef
                        .whereEqualTo("id", productId) // Check if the product exists in the user's cart
                        .get()
                        .addOnSuccessListener { cartItems ->
                            for (cartItem in cartItems.documents) {
                                val cartItemId = cartItem.id
                                // Remove the product from the cart
                                cartRef.document(cartItemId)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Product removed from user's cart", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to remove from cart: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error checking user carts: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error fetching users: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}