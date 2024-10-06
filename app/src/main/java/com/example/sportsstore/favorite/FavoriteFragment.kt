package com.example.sportsstore.favorite

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
import com.example.sportsstore.adapters.ChildAdapterFav
import com.example.sportsstore.databinding.FragmentFavoriteBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.models.FavoriteModel
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment(), ChildAdapterFav.OnItemClickListener {

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

        val adapter = ChildAdapterFav(authViewModel, viewLifecycleOwner, this)
        binding.recyclerFav.adapter = adapter
        binding.recyclerFav.layoutManager = LinearLayoutManager(requireContext())

        try {
            val uid = authViewModel.user.value?.uid
            if (uid == null) {
                Log.e("FavoriteFragment", "User UID is null")
            }

            authViewModel.fetchFavoriteItems()

        } catch (e: Exception) {
            Log.e("FavoriteFragment", "Error fetching favorites", e)
            binding.textView5.text = "Failed to load favorites."
            binding.imageView4.visibility = View.VISIBLE
        }

        authViewModel.favItems.observe(viewLifecycleOwner) { items ->
            if (authViewModel.favItems.value?.isEmpty() == true) {
                binding.imageView4.visibility = View.VISIBLE
                binding.textView5.text = "There are no favorite items yet"
            } else {
                binding.imageView4.visibility = View.GONE
                binding.textView5.text = "${authViewModel.favItems.value?.size} products"
            }

            adapter.setData(items)
        }

        return binding.root
    }

    override fun onItemClick(item: FavoriteModel) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val childItemDeferred = async {
                FirebaseFirestore.getInstance()
                    .collection("sports_shirts")
                    .whereEqualTo("id", item.id)
                    .get()
                    .await()
                    .toObjects(ChildItem::class.java)
            }

            val childItem = childItemDeferred.await()

            withContext(Dispatchers.Main){
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToProductOverviewFragment(childItem.first())
                binding.root.findNavController().navigate(action)
            }
        }
    }
}