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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ParentResearchAdpters
import com.example.sportsstore.databinding.FragmentSearchBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.models.ParentResearchItem
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =FragmentSearchBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]

        val recyclerView = binding.recyclerViewResearchH
        val adapter = ParentResearchAdpters(authViewModel, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bestSellDeferred = async {
                    FirebaseFirestore.getInstance().collection("latest")
                        .get()
                        .await()
                        .toObjects(ChildItem::class.java)
                }

                val itemDisplayDeferred = async {
                    FirebaseFirestore.getInstance().collection("sports_shirts")
                        .get()
                        .await()
                        .toObjects(ChildItem::class.java)
                }

                val bestSell = bestSellDeferred.await()
                val itemDisplay = itemDisplayDeferred.await()

                withContext(Dispatchers.Main){
                    adapter.setData(listOf(
                        ParentResearchItem(bestSell, itemDisplay),
                    ))
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

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