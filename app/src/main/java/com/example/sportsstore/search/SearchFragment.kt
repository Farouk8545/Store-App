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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ChildAdapter
import com.example.sportsstore.databinding.FragmentSearchBinding
import com.example.sportsstore.models.ChildItem




class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =FragmentSearchBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner


        val bestSell: List<ChildItem> = listOf(
            ChildItem("Real Madrid - Home", "2024", 150.0, R.mipmap.real_madrid_home_2024_foreground),
            ChildItem("Real Madrid - Home", "2023", 150.0, R.mipmap.real_madrid_home_2023_foreground),
            ChildItem("Real Madrid - Away", "2023", 150.0, R.mipmap.real_madrid_away_2023_foreground),
            ChildItem("Nike", null, 350.0, R.mipmap.nike_shoes_foreground),
            ChildItem("Sweat Pants", null, 100.0, R.mipmap.sweatpants_foreground),
        )

        val itemDisplay: List<ChildItem> = listOf(
            ChildItem("Real Madrid - Home", "2024", 150.0, R.mipmap.real_madrid_home_2024_foreground),
            ChildItem("Nike", null, 350.0, R.mipmap.nike_shoes_foreground),
            ChildItem("Sweat Pants", null, 100.0, R.mipmap.sweatpants_foreground),
            ChildItem("Adidas", null, 400.0, R.mipmap.adidas_shoes_foreground),
            ChildItem("Real Madrid - Home", "2023", 150.0, R.mipmap.real_madrid_home_2023_foreground),
            ChildItem("Real Madrid - Away", "2023", 150.0, R.mipmap.real_madrid_away_2023_foreground),
            ChildItem("Hat", null, 120.0, R.mipmap.hat_foreground),
            ChildItem("Socks", null, 50.0, R.mipmap.socks_foreground),
        )

        val recyclerView_H = binding.recyclerViewResearchH
        val adapter1 = ChildAdapter()
        recyclerView_H.adapter = adapter1
        recyclerView_H.layoutManager = LinearLayoutManager(requireContext())
        adapter1.setData(bestSell)

        val recyclerView_V = binding.recyclerViewResearchH
        val adapter2 = ChildAdapter()
        recyclerView_V.adapter = adapter2
        recyclerView_V.layoutManager = LinearLayoutManager(requireContext())
        adapter2.setData(itemDisplay)

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