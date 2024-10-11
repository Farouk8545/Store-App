package com.example.sportsstore.models

class CartModel (
    var productName : String?,
    var price :Double,
    var imageUrl : String?,
    var description : String?,
    var quantity : Int,
    var id :String
){
    constructor(): this("",0.0,null,"null",1,"")
}