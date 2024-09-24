package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsstore.R
import com.example.sportsstore.models.ParentItem
import com.example.sportsstore.viewmodels.AuthViewModel

class ParentAdapter(private val authViewModel: AuthViewModel, private val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<ParentAdapter.MyViewHolder>() {
    private var myList = emptyList<ParentItem>()

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val recyclerView: RecyclerView = itemView.findViewById(R.id.horizontalRv)
        val categoryName: TextView = itemView.findViewById(R.id.categoryField)
        val childAdapter = ChildAdapter(authViewModel, lifecycleOwner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false))
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myList[position]
        holder.recyclerView.adapter = holder.childAdapter
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.childAdapter.setData(currentItem.childItemList)
        holder.categoryName.text = currentItem.categoryName
    }
    fun setData(newList: List<ParentItem>){
        myList = newList
        notifyDataSetChanged()
    }
}