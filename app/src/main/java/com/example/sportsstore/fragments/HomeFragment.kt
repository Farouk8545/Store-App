package com.example.sportsstore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ChildAdapter
import com.example.sportsstore.adapters.ParentAdapter
import com.example.sportsstore.databinding.FragmentHomeBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.models.ParentItem
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), ChildAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]

        val recyclerView = binding.verticalRv
        val adapter = ParentAdapter(authViewModel, viewLifecycleOwner, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Fetch both collections in parallel
                val sportsShirtsDeferred = async {
                    FirebaseFirestore.getInstance().collection("sports_shirts")
                        .get()
                        .await()
                        .toObjects(ChildItem::class.java)
                }

                val latestDeferred = async {
                    FirebaseFirestore.getInstance().collection("sports_shirts").orderBy("salesCount", Query.Direction.DESCENDING).limit(5)
                        .get()
                        .await()
                        .toObjects(ChildItem::class.java)
                }

                // Wait for both collections to be fetched
                val sportsShirts = sportsShirtsDeferred.await()
                val latest = latestDeferred.await()

                // Once both are ready, update the adapter on the main thread
                withContext(Dispatchers.Main) {
                    adapter.setData(listOf(
                        ParentItem("Sports Shirts", sportsShirts),
                        ParentItem("Latest", latest)
                    ))
                }
            } catch (e: Exception) {
                // Handle any errors that occur during data fetching
                e.printStackTrace()
            }
        }

        authViewModel.initGoogleSignInClient(requireContext())

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            if(user != null){
                binding.userName.text =
                    if(user.displayName.isNullOrEmpty()) user.email?.substringBefore("@") ?: "User" else user.displayName

                user.photoUrl?.let { uri ->
                    binding.userImage.setImageURI(uri)
                } ?: run {
                    // Set default image if the photo URL is null
                    binding.userImage.setImageResource(R.drawable.baseline_account_circle_24)
                }
            }
        }

        return binding.root
    }

    override fun onItemClick(item: ChildItem) {
        // Handle the click event here, e.g., open a new fragment
        val action = HomeFragmentDirections.actionHomeFragmentToProductOverviewFragment(item)
        findNavController().navigate(action)
    }
}