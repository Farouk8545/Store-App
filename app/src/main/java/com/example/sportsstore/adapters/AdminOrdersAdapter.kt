package com.example.sportsstore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportsstore.databinding.OrdersRowLayoutBinding
import com.example.sportsstore.models.OrderModel
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

class AdminOrdersAdapter(private val authViewModel: AuthViewModel):RecyclerView.Adapter<AdminOrdersAdapter.MyViewHolder>() {
    private var myList: MutableList<OrderModel> = mutableListOf()

    inner class MyViewHolder(private val binding: OrdersRowLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindData(order: OrderModel){
            Glide.with(binding.productImage).load(order.imageUrl).into(binding.productImage)
            binding.productNameText.text = order.productName
            binding.productYearText.text = order.year
            binding.productPriceText.text = order.price.toString()
            binding.customerEmailText.text = order.email
            binding.customerPhoneNumberText.text = order.phoneNumber
            binding.customerAddressText.text = order.address
            binding.amountText.text = order.amount.toString()
            binding.colorText.text = order.selectedColor
            binding.sizeText.text = order.selectedSize
            binding.dateText.text = order.date?.toDate().toString()
            binding.paymentMethodText.text = order.paymentMethod
            binding.deliveryStateText.text = order.state

            binding.changeToDeliveredButton.setOnClickListener {
                val orderRef = FirebaseFirestore.getInstance().collection("orders").whereEqualTo("id", order.id)
                orderRef.get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        for(document in task.result){
                            document.reference.update("state", "Delivered")
                        }
                    }
                }
                val purchaseRef = FirebaseFirestore.getInstance().collection("users").document(order.userId).collection("purchases").whereEqualTo("id", order.id)

                purchaseRef.get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        for(document in task.result) document.reference.update("state", "Delivered")
                    }
                }
                deleteData(order)
            }

            binding.cancelOrderButton.setOnClickListener {
                val orderRef = FirebaseFirestore.getInstance().collection("orders").whereEqualTo("id", order.id)
                orderRef.get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        for(document in task.result){
                            document.reference.update("state", "Canceled")
                        }
                    }
                }
                val purchaseRef = FirebaseFirestore.getInstance().collection("users").document(order.userId).collection("purchases").whereEqualTo("id", order.id)

                purchaseRef.get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        for(document in task.result) document.reference.update("state", "Canceled")
                    }
                }
                deleteData(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = OrdersRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(myList[position])
    }

    fun setData(newList: MutableList<OrderModel>){
        myList = newList
        notifyDataSetChanged()
    }

    private fun deleteData(order: OrderModel){
        myList.remove(order)
        notifyDataSetChanged()
    }

    fun getData(): List<OrderModel> = myList
}