package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsstore.databinding.PurchaseHistoryRowLayoutBinding
import com.example.sportsstore.models.PurchaseModel
import com.example.sportsstore.viewmodels.AuthViewModel

class PurchaseHistoryAdapter(private val authViewModel: AuthViewModel): RecyclerView.Adapter<PurchaseHistoryAdapter.MyViewHolder>() {
    private var purchaseList = emptyList<PurchaseModel>()
    inner class MyViewHolder(private val binding: PurchaseHistoryRowLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindDate(purchase: PurchaseModel){
            binding.productName = purchase.product
            binding.productPrice = (purchase.price * purchase.amount).toString()
            binding.purchaseDate = purchase.date.toDate().toString()
            binding.productState = purchase.state
            binding.paymentMethod = purchase.paymentMethod
            binding.color = purchase.color
            binding.size = purchase.size
            binding.amount = purchase.amount.toString()
            Glide.with(binding.productImage).load(purchase.imageUrl).into(binding.productImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = PurchaseHistoryRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = purchaseList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindDate(purchaseList[position])
    }

    fun setData(newList: List<PurchaseModel>){
        purchaseList = newList
        notifyDataSetChanged()
    }

    fun getData(): List<PurchaseModel>{
        return purchaseList
    }

}