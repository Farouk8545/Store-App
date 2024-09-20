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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ChildAdapter
import com.example.sportsstore.adapters.ParentAdapter
import com.example.sportsstore.databinding.FragmentSearchBarBinding
import com.example.sportsstore.models.ChildItem
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class SearchBarFragment : Fragment() {

    private lateinit var binding :FragmentSearchBarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =FragmentSearchBarBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner

        var itemDisplay: MutableList<ChildItem> = mutableListOf()
        FirebaseFirestore.getInstance().collection("sports_shirts")
            .get().addOnSuccessListener {
                itemDisplay = it.toObjects(ChildItem::class.java)
            }

        val recyclerView = binding.recyclerView
        val adapter = ChildAdapter()
        recyclerView.adapter = adapter
        adapter.setDataSearch(emptyList()) // Start with an empty list
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns

        fun filterlist(newText: String?): Boolean {
            val filterList = ArrayList<ChildItem>()

            // If the search text is empty, set the list to empty
            if (newText.isNullOrEmpty()) {
                adapter.setDataSearch(emptyList()) // Show no items when search is cleared
                return true
            }

            // Perform filtering based on the search text
            for (i in itemDisplay) {
                if (i.productName.lowercase().contains(newText.lowercase())) {
                    filterList.add(i)
                }
            }

            // Display filtered items, or show a toast if no matches found
            if (filterList.isEmpty()) {
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                return false
            } else {
                adapter.setDataSearch(filterList) // Set filtered data
                return true
            }

        }

        binding.searchView2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return filterlist(newText)
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.imageButton3.setOnClickListener{
            findNavController().navigate(R.id.action_searchBarFragment2_to_searchFragment2)
        }
    }

}