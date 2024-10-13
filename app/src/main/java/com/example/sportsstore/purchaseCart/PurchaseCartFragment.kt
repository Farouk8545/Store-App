package com.example.sportsstore.purchaseCart

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ChildAdapterCart
import com.example.sportsstore.databinding.FragmentPurchaseCardBinding
import com.example.sportsstore.models.CartModel
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class PurchaseCartFragment : Fragment(), ChildAdapterCart.OnItemClickListener {
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

        try {
            val uid = authViewModel.user.value?.uid
            if (uid == null) {
                Log.e("cartFragment", "User UID is null")
            }
            authViewModel.fetchCartItems()

        } catch (e: Exception) {
            Log.e("CartFragment", "Error fetching Cart", e)
            binding.textView5.text = getString(R.string.failed_to_load_cart)
            binding.imageView4.visibility = View.VISIBLE
        }

        authViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (authViewModel.cartItems.value?.isEmpty() == true) {
                binding.imageView4.visibility = View.VISIBLE
                binding.buyAllButton.visibility = View.GONE
                binding.textView5.text = getString(R.string.card_is_empty)
            } else {
                binding.imageView4.visibility = View.GONE
                binding.buyAllButton.visibility = View.VISIBLE
                if(authViewModel.cartItems.value?.size == 1){
                    binding.textView5.text = "${authViewModel.cartItems.value?.size} product"
                }else{
                    binding.textView5.text = "${authViewModel.cartItems.value?.size} products"
                }

            }

            binding.buyAllButton.setOnClickListener {
                if (adapter.getItemsCount() != 0){
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        val childItemsDeferred = async {
                            adapter.getItemsIds().map { id ->
                                async {
                                    FirebaseFirestore.getInstance()
                                        .collection("sports_shirts")
                                        .whereEqualTo("id", id)
                                        .get()
                                        .await()
                                        .toObjects(ChildItem::class.java)
                                }
                            }.awaitAll().flatten()
                        }

                        val childItems = childItemsDeferred.await()

                        if(childItems.isNotEmpty()){
                            withContext(Dispatchers.Main){
                                val action = PurchaseCartFragmentDirections.actionPurchaseCartFragment2ToPaymentFragment(
                                    childItems.toTypedArray(),
                                    adapter.getItemsQuantity().toIntArray(),
                                    adapter.getSelectedColor().toTypedArray(),
                                    adapter.getSelectedSize().toTypedArray(),
                                    adapter.getTotalCost().toFloatArray(),
                                    true
                                    )
                                binding.root.findNavController().navigate(action)
                            }
                        }
                    }
                }
            }

            adapter.setData(items)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onItemClick(item: CartModel) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val childItemDeferred = async {
                    FirebaseFirestore.getInstance()
                        .collection("sports_shirts")
                        .whereEqualTo("id", item.id)
                        .get()
                        .await()
                        .toObjects(ChildItem::class.java)
                }

                val childItem = childItemDeferred.await()

                if (childItem.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        val action = PurchaseCartFragmentDirections.actionPurchaseCartFragment2ToProductOverviewFragment(childItem.toTypedArray())
                        binding.root.findNavController().navigate(action)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("PurchaseCartFragment", "Item not found")
                        // Optionally notify the user that the item is no longer available
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("PurchaseCartFragment", "Error fetching item", e)
                }
            }
        }
    }
}
