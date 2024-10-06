package com.example.sportsstore.models

class FavoriteModel(
    val product: String,
    val price: Double,
    val imageUrl: String?,
    val description: String?,
    val id: String
) {
    constructor() : this("", 0.0, null, null, "")
}