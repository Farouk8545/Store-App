package com.example.sportsstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ChildItem(
    val productName: String,
    val year: String?,
    val price: Double,
    val imageUrl: String?,
    val description: String?,
    val id: String,
    val colors: List<String>?,
    val sizes: List<String>?,
): Parcelable
{
    constructor() : this("", null, 0.0, null, null, "", null, null)

}