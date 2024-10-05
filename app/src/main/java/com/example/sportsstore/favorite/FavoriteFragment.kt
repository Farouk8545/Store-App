package com.example.sportsstore.favorite

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
import com.example.sportsstore.adapters.ChildAdapter
import com.example.sportsstore.adapters.ChildAdapterFav
import com.example.sportsstore.databinding.FragmentFavoriteBinding
import com.example.sportsstore.databinding.FragmentSearchBarBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.models.FavoriteModel
import com.example.sportsstore.models.ParentItem
import com.example.sportsstore.models.User
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        authViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AuthViewModel::class.java]

        val adapter = ChildAdapterFav(authViewModel, this)
        binding.recyclerFav.adapter = adapter
        binding.recyclerFav.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uid = authViewModel.user.value?.uid
                if (uid == null) {
                    Log.e("FavoriteFragment", "User UID is null")
                    return@launch
                }

                // Fetch favorites data asynchronously
                val favouritesDeferred = async {
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .collection("favorites")
                        .get()
                        .await()
                        .toObjects(FavoriteModel::class.java)
                }

                // Await the results of the asynchronous Firestore query
                val favourites = favouritesDeferred.await()

                withContext(Dispatchers.Main) {
                    if (favourites.isEmpty()) {
                        binding.imageView4.visibility = View.VISIBLE
                        binding.textView5.text = "There are no favorite items yet"
                    } else {
                        binding.imageView4.visibility = View.GONE
                        binding.textView5.text = "${favourites.size} products"
                    }

                    // Update adapter data
                    adapter.setData(favourites)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("FavoriteFragment", "Error fetching favorites", e)
                    binding.textView5.text = "Failed to load favorites."
                    binding.imageView4.visibility = View.VISIBLE
                }
            }
        }

        return binding.root
    }
}