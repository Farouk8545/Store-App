package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsstore.R
import com.example.sportsstore.models.ParentResearchItem
import com.example.sportsstore.viewmodels.AuthViewModel

class ParentResearchAdpters(
    private val authViewModel: AuthViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onItemClickListener: ChildAdapter.OnItemClickListener
) : RecyclerView.Adapter<ParentResearchAdpters.MyViewHolder>() {

    private var myList1 = emptyList<ParentResearchItem>()

    // ViewHolder class for holding the two RecyclerViews
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView1: RecyclerView = itemView.findViewById(R.id.recyclerView_sea_H)
        val recyclerView2: RecyclerView = itemView.findViewById(R.id.recyclerView_sea_V)

        // Two different child adapters for each RecyclerView
        val childAdapter1 = ChildAdapter(authViewModel, lifecycleOwner, onItemClickListener)
        val childAdapter2 = ChildAdapter(authViewModel, lifecycleOwner, onItemClickListener)
    }

    // Create ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_search, parent, false)
        return MyViewHolder(view)
    }

    // Return the size of the list
    override fun getItemCount(): Int = myList1.size

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myList1[position]

        // Setup RecyclerView 1 with horizontal LinearLayoutManager and set data
        holder.recyclerView1.adapter = holder.childAdapter1
        holder.recyclerView1.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.childAdapter1.setData(currentItem.childItemList1)

        // Setup RecyclerView 2 with GridLayoutManager and set data
        holder.recyclerView2.adapter = holder.childAdapter2
        holder.recyclerView2.layoutManager = GridLayoutManager(holder.itemView.context, 2) // 2 columns
        holder.childAdapter2.setData(currentItem.childItemList2)
    }

    // Update the list and notify the adapter
    fun setData(newList: List<ParentResearchItem>) {
        myList1 = newList
        notifyDataSetChanged()
    }
}