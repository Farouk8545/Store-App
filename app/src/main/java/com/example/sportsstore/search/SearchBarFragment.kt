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
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentSearchBarBinding


class SearchBarFragment : Fragment() {

    private lateinit var binding :FragmentSearchBarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =FragmentSearchBarBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}