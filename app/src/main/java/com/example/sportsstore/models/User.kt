package com.example.sportsstore.models

class User(
    val email: String,
    val displayName: String?,
    val phoneNumber: String,
    val address: String
) {
    constructor(): this("", null, "", "")
}