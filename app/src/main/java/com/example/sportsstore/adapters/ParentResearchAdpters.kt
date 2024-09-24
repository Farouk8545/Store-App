package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsstore.R
import com.example.sportsstore.models.ParentItem
import com.example.sportsstore.models.ParentResearchItem
import com.example.sportsstore.viewmodels.AuthViewModel

class ParentResearchAdpters(private val authViewModel: AuthViewModel, private val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<ParentResearchAdpters.MyViewHolder>(){

    private var myList = emptyList<ParentResearchItem>()


    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val recyclerView1: RecyclerView = itemView.findViewById(R.id.recyclerView_sea_H)
        val recyclerView2: RecyclerView = itemView.findViewById(R.id.recyclerView_sea_V)
        val childAdapter = ChildAdapter(authViewModel, lifecycleOwner)

        //val childAdapter2 = ChildAdapter()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_layout_search, parent, false))
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myList[position]
        holder.recyclerView1.adapter = holder.childAdapter
        holder.recyclerView2.adapter = holder.childAdapter
        holder.recyclerView1.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerView2.layoutManager = GridLayoutManager(holder.itemView.context, 2) // 2 columns
        holder.childAdapter.setData(currentItem.childItemList1)
        holder.childAdapter.setData(currentItem.childItemList2)
    }


    fun setData(newList: List<ParentResearchItem>){
        myList = newList
        notifyDataSetChanged()
    }
}