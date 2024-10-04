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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChildAdapter(
    private val authViewModel: AuthViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ChildAdapter.MyViewHolder>() {
    private var myList = emptyList<ChildItem>()

    interface OnItemClickListener {
        fun onItemClick(item: ChildItem)
    }

    inner class MyViewHolder(private val binding: ColumnLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindData(item: ChildItem){
            binding.productName.text = item.productName
            binding.year.text = item.year
            binding.price.text = item.price.toString()

            GlobalScope.launch {
                if(authViewModel.inFavorite(item.id)) binding.favouriteButton.setImageResource(R.drawable.baseline_favorite_24)
                else binding.favouriteButton.setImageResource(R.drawable.ic_not_chosen_favorite_icon)
            }

            Glide.with(binding.productImage).load(item.imageUrl).placeholder(R.drawable.baseline_image_24).into(binding.productImage)

            binding.favouriteButton.setOnClickListener {
                GlobalScope.launch {
                    if(authViewModel.inFavorite(item.id)){
                        authViewModel.deleteFavorite(item.id)
                        binding.favouriteButton.setImageResource(R.drawable.ic_not_chosen_favorite_icon)
                    }else{
                        authViewModel.addFavorite(
                            item.productName,
                            item.price,
                            item.imageUrl,
                            item.description,
                            item.id
                        )
                        binding.favouriteButton.setImageResource(R.drawable.baseline_favorite_24)
                    }
                }
            }

            binding.root.setOnClickListener {
                onItemClickListener.onItemClick(item)
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