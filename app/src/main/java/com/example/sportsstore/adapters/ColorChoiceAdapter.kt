package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsstore.databinding.RadioButtonLayoutBinding

class ColorChoiceAdapter(private val colors: List<String>): RecyclerView.Adapter<ColorChoiceAdapter.MyViewHolder>() {
    private var selectedPosition = -1

    inner class MyViewHolder(private val binding: RadioButtonLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindDate(color: String, position: Int){
            binding.radioButton.text = color
            binding.radioButton.isChecked = position == selectedPosition

            binding.radioButton.setOnClickListener{
                if(position != selectedPosition){
                    val oldPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RadioButtonLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = colors.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindDate(colors[position], position)
    }
}