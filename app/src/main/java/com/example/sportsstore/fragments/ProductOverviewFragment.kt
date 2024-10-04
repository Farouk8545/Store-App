package com.example.sportsstore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.sportsstore.R
import com.example.sportsstore.adapters.ColorChoiceAdapter
import com.example.sportsstore.databinding.FragmentProductOverviewBinding

class ProductOverviewFragment : Fragment() {
    private lateinit var binding: FragmentProductOverviewBinding
    private val args by navArgs<ProductOverviewFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductOverviewBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setPadding(0, 0, 0, 0)
        toolbar.setContentInsetsAbsolute(0, 0)

        val layoutParams = androidx.appcompat.widget.Toolbar.LayoutParams(
            androidx.appcompat.widget.Toolbar.LayoutParams.MATCH_PARENT,
            androidx.appcompat.widget.Toolbar.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = android.view.Gravity.CENTER  // Center the custom view in the Toolbar

        val customToolbar = LayoutInflater.from(requireContext()).inflate(R.layout.product_overview_toolbar_custom_layout, toolbar, false)
        toolbar.addView(customToolbar, layoutParams)

        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        val recyclerView = binding.colorRv
        val adapter = args.currentProduct.colors?.let { ColorChoiceAdapter(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val spinnerAdapter =
            context?.let {
                ArrayAdapter(
                    it, android.R.layout.simple_spinner_item,
                    args.currentProduct.sizes ?: emptyList()
                )
            }
        spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sizeSpinner.adapter = spinnerAdapter

        Glide.with(binding.productImage).load(args.currentProduct.imageUrl).into(binding.productImage)
        binding.productNameText.text = args.currentProduct.productName
        binding.productYearText.text = args.currentProduct.year
        binding.productDescription.text = args.currentProduct.description

        return binding.root
    }
}