package com.example.sportsstore.purchaseCart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ChildAdapterCart
import com.example.sportsstore.adapters.ChildAdapterFav
import com.example.sportsstore.databinding.FragmentFavoriteBinding
import com.example.sportsstore.databinding.FragmentPurchaseCardBinding
import com.example.sportsstore.models.CartModel
import com.example.sportsstore.models.FavoriteModel
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class PurchaseCartFragment : Fragment() {
    lateinit var binding: FragmentPurchaseCardBinding
    private lateinit var authViewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPurchaseCardBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner

        authViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AuthViewModel::class.java]

        val adapter = ChildAdapterCart(authViewModel, this)
        binding.recyclerCart.adapter = adapter
        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())


        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uid = authViewModel.user.value?.uid
                if (uid == null) {
                    Log.e("cartFragment", "User UID is null")
                    return@launch
                }

                // Fetch favorites data asynchronously
                val favouritesDeferred = async {
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .collection("purchases_cart")
                        .get()
                        .await()
                        .toObjects(CartModel::class.java)
                }

                // Await the results of the asynchronous Firestore query
                val favourites = favouritesDeferred.await()

                withContext(Dispatchers.Main) {
                    if (favourites.isEmpty()) {
                        binding.imageView4.visibility = View.VISIBLE
                        binding.textView5.text = "card is empty"
                    } else {
                        binding.imageView4.visibility = View.GONE
                        binding.textView5.text = "${favourites.size} products"
                    }

                    // Update adapter data
                    adapter.setData(favourites)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CartFragment", "Error fetching Cart", e)
                    binding.textView5.text = "Failed to load cart."
                    binding.imageView4.visibility = View.VISIBLE
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }


}