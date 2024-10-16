package com.example.sportsstore.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.AdminOrdersAdapter
import com.example.sportsstore.databinding.FragmentOrdersBinding
import com.example.sportsstore.models.OrderModel
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private lateinit var adapter: AdminOrdersAdapter
    private lateinit var authViewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]
        adapter = AdminOrdersAdapter(authViewModel)
        binding.orderRv.adapter = adapter
        binding.orderRv.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            val ordersDeferred = async {
                FirebaseFirestore.getInstance().collection("orders").whereEqualTo("state", "undelivered")
                    .get()
                    .await()
                    .toObjects(OrderModel::class.java)
            }

            val orders = ordersDeferred.await()

            withContext(Dispatchers.Main){
                adapter.setData(orders)
                if(adapter.getData().isEmpty()){
                    binding.emptyText.visibility = View.VISIBLE
                }else{
                    binding.emptyText.visibility = View.GONE
                }
            }
        }

        return binding.root
    }
}