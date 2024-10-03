package com.example.sportsstore.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ChildAdapter
import com.example.sportsstore.adapters.ChildAdapterFav
import com.example.sportsstore.databinding.FragmentFavoriteBinding
import com.example.sportsstore.databinding.FragmentSearchBarBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.models.FavoriteModel
import com.example.sportsstore.models.ParentItem
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
    private var itemDisplay: MutableList<ChildItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner

        authViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AuthViewModel::class.java]


        val adapter = ChildAdapterFav(authViewModel, this)
        binding.recyclerFav.adapter = adapter
        binding.recyclerFav.layoutManager = LinearLayoutManager(requireContext())

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Fetch both collections in parallel
                val favouriteDeferred = async {
                    FirebaseFirestore.getInstance().collection("users").document(authViewModel.user.value?.uid!!).collection("favorites")
                        .get()
                        .await()
                        .toObjects(FavoriteModel::class.java)
                }


                // Wait for both collections to be fetched
                val favourites = favouriteDeferred.await()

                binding.textView5.text = " ${favourites.size} products"
                // Once both are ready, update the adapter on the main thread
                withContext(Dispatchers.Main) {
                    adapter.setData(
                        favourites,
                    )
                }
            } catch (e: Exception) {
                // Handle any errors that occur during data fetching
                e.printStackTrace()
            }
        }

        return binding.root
    }

}