package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsstore.R
import com.example.sportsstore.databinding.ColumFavLayoutBinding
import com.example.sportsstore.databinding.ColumnLayoutBinding
import com.example.sportsstore.models.ChildItem
import com.example.sportsstore.models.FavoriteModel
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.Timestamp

class ChildAdapterFav(private val authViewModel: AuthViewModel, private val lifecycleOwner: LifecycleOwner)
    : RecyclerView.Adapter<ChildAdapterFav.MyViewHolder>() {
    private var myList = emptyList<FavoriteModel>()

    inner class MyViewHolder(private val binding: ColumFavLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindData(item: FavoriteModel){
            binding.textView7.text = item.price.toString()
            Glide.with(binding.imageView3).load(item.imageUrl).placeholder(R.drawable.baseline_image_24).into(binding.imageView3)
            binding.textView8.text = item.description
        }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ColumFavLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int  = myList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(myList[position])
    }


    fun setData(newList: List<FavoriteModel>){
        myList = newList
        notifyDataSetChanged()
    }
}