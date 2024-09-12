package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsstore.R
import com.example.sportsstore.models.ChildItem

class ChildAdapter(): RecyclerView.Adapter<ChildAdapter.MyViewHolder>() {
    private var myList = emptyList<ChildItem>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val price: TextView = itemView.findViewById(R.id.price)
        val year: TextView = itemView.findViewById(R.id.year)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.column_layout, parent, false))
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myList[position]
        holder.productImage.setImageResource(currentItem.imageUrl)
        holder.productName.text = currentItem.productName
        holder.price.text = currentItem.price.toString() + "EGP"
        holder.year.text = currentItem.year ?: ""
    }

    fun setData(newList: List<ChildItem>){
        myList = newList
        notifyDataSetChanged()
    }
}