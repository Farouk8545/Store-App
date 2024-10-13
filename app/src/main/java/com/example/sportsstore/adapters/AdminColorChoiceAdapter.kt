package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsstore.databinding.AdminColorChoiceLayoutBinding

class AdminColorChoiceAdapter: RecyclerView.Adapter<AdminColorChoiceAdapter.MyViewHolder>() {
    private var list: MutableList<String> = emptyList<String>().toMutableList()

    inner class MyViewHolder(private val binding: AdminColorChoiceLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindData(item: String){
            binding.colorText.text = item

            binding.root.setOnClickListener {
                list.remove(item)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AdminColorChoiceLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    fun setData(newList: List<String>){
        list = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun setData(newItem: String){
        list.add(newItem)
        notifyDataSetChanged()
    }

    fun getData(): MutableList<String>{
        return list
    }
}