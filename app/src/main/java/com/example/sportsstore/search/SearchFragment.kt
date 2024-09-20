/*
 * File:   SearchFragment
 * Author: mokhtar
 * Created on september 15, 2024, 11:26 AM
 */
package com.example.sportsstore.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ParentResearchAdpters
import com.example.sportsstore.databinding.FragmentSearchBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.models.ParentResearchItem
import com.google.firebase.firestore.FirebaseFirestore


class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =FragmentSearchBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner

        var bestSell: MutableList<ChildItem> = mutableListOf()
        var itemDisplay: MutableList<ChildItem> = mutableListOf()
        FirebaseFirestore.getInstance().collection("sports_shirts")
            .get().addOnSuccessListener {
                bestSell = it.toObjects(ChildItem::class.java)
                itemDisplay = it.toObjects(ChildItem::class.java)
            }

        val recyclerView = binding.recyclerViewResearchH
        val adapter = ParentResearchAdpters()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter.setData(listOf(
            ParentResearchItem(bestSell, itemDisplay),
        ))


        /*
        Author: Farouk Haitham
        fixed the recycler view h instead of v
        removed unused list
        removed unused imports
         */

        return binding.root
    }


    /*
    * replaced the actions of the search nav graph with the actions of the nav_graph
    * deleted the search nav graph*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.button2.setOnClickListener{
            findNavController().navigate(R.id.action_searchFragment2_to_searchBarFragment2)
        }
    }

}