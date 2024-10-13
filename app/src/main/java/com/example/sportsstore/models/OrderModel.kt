package com.example.sportsstore.models

import com.google.firebase.Timestamp

data class OrderModel(
    val productName: String,
    val year: String,
    val address: String,
    val amount: Int,
    val date: Timestamp?,
    val email: String,
    val id: String,
    val imageUrl: String,
    val paymentMethod: String,
    val phoneNumber: String,
    val price: Double,
    val selectedColor: String,
    val selectedSize: String,
    val state: String,
    val userId: String
){
    constructor(): this("", "", "", 0, null, "", "", "", "", "", 0.0, "", "", "", "")
}