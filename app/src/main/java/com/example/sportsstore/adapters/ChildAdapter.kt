package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsstore.R
import com.example.sportsstore.databinding.ColumnLayoutBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.Timestamp

class ChildAdapter(private val authViewModel: AuthViewModel, private val lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<ChildAdapter.MyViewHolder>() {
    private var myList = emptyList<ChildItem>()

    inner class MyViewHolder(private val binding: ColumnLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindData(item: ChildItem){
            binding.productName.text = item.productName
            binding.year.text = item.year
            binding.price.text = item.price.toString()
            Glide.with(binding.productImage).load(item.imageUrl).placeholder(R.drawable.baseline_image_24).into(binding.productImage)

            binding.root.setOnClickListener {
                    authViewModel.purchaseDocument()
                    authViewModel.addPurchase(
                        item.productName,
                        item.price,
                        Timestamp.now(),
                        "Pending",
                        "Paypal",
                        item.imageUrl
                    )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ColumnLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(myList[position])
    }

    fun setData(newList: List<ChildItem>){
        myList = newList
        notifyDataSetChanged()
    }

    fun setDataSearch(newList: List<ChildItem>?){
        if (newList != null) {
            myList = newList
        }
        notifyDataSetChanged()
    }
}