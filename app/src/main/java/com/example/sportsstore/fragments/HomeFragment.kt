package com.example.sportsstore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
                val latestDeferred = async {
                    FirebaseFirestore.getInstance().collection("sports_shirts").orderBy("createdAt", Query.Direction.DESCENDING).limit(10)
                        .get()
                        .await()
                        .toObjects(ChildItem::class.java)
                }

                val sportsShirtsDeferred = async {
                    FirebaseFirestore.getInstance().collection("sports_shirts").whereEqualTo("category", "Sports Shirts").orderBy("salesCount", Query.Direction.DESCENDING).limit(10)
                        .get()
                        .await()
                        .toObjects(ChildItem::class.java)
                }

                val sportsShoesDeferred = async {
                    FirebaseFirestore.getInstance().collection("sports_shirts").whereEqualTo("category", "Sports Shoes").orderBy("salesCount", Query.Direction.DESCENDING).limit(10)
                        .get()
                        .await()
                        .toObjects(ChildItem::class.java)
                }

                val sportsWearsDeferred = async {
                    FirebaseFirestore.getInstance().collection("sports_shirts").whereEqualTo("category", "Sports Wears").orderBy("salesCount", Query.Direction.DESCENDING).limit(10)
                        .get()
                        .await()
                        .toObjects(ChildItem::class.java)
                }

                // Wait for both collections to be fetched
                val sportsShirts = sportsShirtsDeferred.await()
                val latest = latestDeferred.await()
                val sportsShoes = sportsShoesDeferred.await()
                val sportsWears = sportsWearsDeferred.await()

                // Once both are ready, update the adapter on the main thread
                withContext(Dispatchers.Main) {
                    adapter.setData(listOf(
                        ParentItem(getString(R.string.latest), latest),
                        ParentItem(getString(R.string.sportsshirts), sportsShirts),
                        ParentItem(getString(R.string.sports_shoes), sportsShoes),
                        ParentItem(getString(R.string.sports_wears), sportsWears)
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
                    Glide.with(binding.userImage)
                        .load(uri)
                        .placeholder(R.drawable.baseline_account_circle_24)
                        .error(R.drawable.baseline_account_circle_24)
                        .into(binding.userImage)
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
        val action = HomeFragmentDirections.actionHomeFragmentToProductOverviewFragment(arrayOf(item))
        findNavController().navigate(action)
    }
}