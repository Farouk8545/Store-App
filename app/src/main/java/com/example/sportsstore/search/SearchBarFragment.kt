/*
 * File:   SearchBarFragment
 * Author: mokhtar
 * Created on september 15, 2024, 11:26 AM
 */

package com.example.sportsstore.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ChildAdapter
import com.example.sportsstore.adapters.ParentAdapter
import com.example.sportsstore.databinding.FragmentSearchBarBinding
import com.example.sportsstore.fragments.HomeFragmentDirections
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class SearchBarFragment : Fragment(), ChildAdapter.OnItemClickListener {

    private lateinit var binding: FragmentSearchBarBinding
    private lateinit var authViewModel: AuthViewModel
    private var itemDisplay: MutableList<ChildItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBarBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        authViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AuthViewModel::class.java]

        val adapter = ChildAdapter(authViewModel, viewLifecycleOwner, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Initially show an empty list
        adapter.setDataSearch(emptyList())

        // Filter function for search results after fetching data
        fun filterlist(newText: String?, fetchedItems: MutableList<ChildItem>): Boolean {
            val filterList = ArrayList<ChildItem>()

            if (newText.isNullOrEmpty()) {
                adapter.setDataSearch(emptyList())
                return true
            }

            // Perform the filtering
            for (i in fetchedItems) {
                if (i.productName.lowercase().contains(newText!!.lowercase())) {
                    filterList.add(i)
                }
            }

            // If no matching items, show a toast
            if (filterList.isEmpty()) {
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                return false
            } else {
                adapter.setDataSearch(filterList)
                return true
            }
        }

        // Function to fetch data from Firebase and filter it based on the query
        fun performSearch(query: String?) {
            FirebaseFirestore.getInstance().collection("sports_shirts")
                .get()
                .addOnSuccessListener { documents ->
                    itemDisplay = documents.toObjects(ChildItem::class.java)
                    filterlist(query, itemDisplay)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
        }

        // Set SearchView listener for both enter and search icon clicks
        binding.searchView2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                // Do nothing when the user is typing
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView2.clearFocus()  // Clear focus on submit
                performSearch(query)  // Fetch and filter data
                return true
            }
        })

        // Set listener for the search button (the magnifying glass icon)
        binding.searchView2.setOnClickListener {
            val query = binding.searchView2.query.toString()
            performSearch(query)  // Fetch and filter data when search icon is clicked
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.imageButton3.setOnClickListener {
            findNavController().navigate(R.id.action_searchBarFragment2_to_searchFragment2)
        }
    }

    override fun onItemClick(item: ChildItem) {
        val action = SearchBarFragmentDirections.actionSearchBarFragment2ToProductOverviewFragment(arrayOf(item))
        findNavController().navigate(action)
    }
}