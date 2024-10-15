package com.example.sportsstore.models

import com.google.firebase.Timestamp

class PurchaseModel(
    val product: String,
    val price: Double,
    val date: Timestamp,
    val state: String,
    val paymentMethod: String,
    val imageUrl: String?,
    val color: String,
    val size: String,
    val amount: Int
) {
    constructor() : this("", 0.0, Timestamp.now(), "", "", null, "", "", 0)
}