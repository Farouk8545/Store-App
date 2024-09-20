package com.example.sportsstore.models

class ChildItem(
    val productName: String,
    val year: String?,
    val price: Double,
    val imageUrl: String?,
    val description: String?
){
    constructor() : this("", null, 0.0, null, null)

}