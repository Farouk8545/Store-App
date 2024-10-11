package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsstore.R
import com.example.sportsstore.databinding.ColumCartLayoutBinding
import com.example.sportsstore.models.CartModel
import com.example.sportsstore.viewmodels.AuthViewModel

class ChildAdapterCart(
    private val authViewModel: AuthViewModel,
    private val onItemClickListener: OnItemClickListener
)
    : RecyclerView.Adapter<ChildAdapterCart.MyViewHolder>() {

    private var myList = emptyList<CartModel>()

    interface OnItemClickListener {
        fun onItemClick(item: CartModel)
    }

    inner class MyViewHolder(private val binding: ColumCartLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindData(item: CartModel){
            binding.textView7.text = item.price.toString()
            Glide.with(binding.imageView3).load(item.imageUrl).placeholder(R.drawable.baseline_image_24).into(binding.imageView3)
            binding.textView8.text = item.description
            binding.productNameCart.text = item.productName.toString()
            binding.itemCount.text = item.quantity.toString()
            binding.colorText.text = "Color: ${item.color}"
            binding.sizeText.text = "Size: ${item.size}"
            authViewModel.updateCartItemQuantity(item.id, item.quantity)

            // Disable remove button if quantity is 1
            binding.buttonRemove.isEnabled = item.quantity > 1

            binding.buttonAdd.setOnClickListener {
                item.quantity++ // Increment quantity
                binding.itemCount.text = item.quantity.toString()
                binding.buttonRemove.isEnabled = true // Re-enable the remove button

                // Update quantity in Firebase
                authViewModel.updateCartItemQuantity(item.id, item.quantity)
            }

            // Handle Remove Button Click
            binding.buttonRemove.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity-- // Decrement quantity
                    binding.itemCount.text = item.quantity.toString()

                    // Disable remove button if quantity is 1
                    if (item.quantity == 1) {
                        binding.buttonRemove.isEnabled = false
                    }

                    // Update quantity in Firebase
                    authViewModel.updateCartItemQuantity(item.id, item.quantity)
                }
            }

            // Handle delete button
            binding.deleteButton.setOnClickListener {
                authViewModel.deletePurchaseCart(item.id)
            }



            binding.root.setOnClickListener{
                onItemClickListener.onItemClick(item)
            }

            binding.deleteButton.setOnClickListener {
                authViewModel.deletePurchaseCart(item.id)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildAdapterCart.MyViewHolder {
        val binding = ColumCartLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
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

    fun getItemsIds(): List<String> {
        return myList.map { it.id }
    }

    fun getItemsCount(): Int {
        return myList.size
    }

    fun getItemsQuantity(): List<Int>{
        return myList.map { it.quantity }
    }

    fun getSelectedColor(): List<String>{
        return myList.map { it.color }
    }

    fun getSelectedSize(): List<String>{
        return myList.map { it.size }
    }

    fun getTotalCost(): List<Float>{
        return myList.map { it.price.toFloat() * it.quantity.toFloat() }
    }
}