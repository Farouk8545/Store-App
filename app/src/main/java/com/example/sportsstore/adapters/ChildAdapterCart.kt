package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsstore.R
import com.example.sportsstore.databinding.ColumCartLayoutBinding
import com.example.sportsstore.databinding.ColumFavLayoutBinding
import com.example.sportsstore.models.CartModel
import com.example.sportsstore.models.FavoriteModel
import com.example.sportsstore.viewmodels.AuthViewModel

class ChildAdapterCart(private val authViewModel: AuthViewModel, private val lifecycleOwner: LifecycleOwner)
    : RecyclerView.Adapter<ChildAdapterCart.MyViewHolder>() {

    private var myList = emptyList<CartModel>()

    inner class MyViewHolder(private val binding: ColumCartLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindData(item: CartModel){
            binding.textView7.text = item.price.toString()
            Glide.with(binding.imageView3).load(item.imageUrl).placeholder(R.drawable.baseline_image_24).into(binding.imageView3)
            binding.textView8.text = item.description
            binding.textView6.text = item.product
            binding.imageView5.setOnClickListener{

            }
        }

    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildAdapterCart.MyViewHolder {
        var binding = ColumCartLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildAdapterCart.MyViewHolder, position: Int) {
        holder.bindData(myList[position])
    }

    override fun getItemCount(): Int = myList.size

    fun setData(newList: List<CartModel>){
        myList = newList
        notifyDataSetChanged()
    }


}