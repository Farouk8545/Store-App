package com.example.sportsstore.models

class CartModel (
    var product : String?,
    var price :Double,
    var imageUrl : String?,
    var description : String?,
    var id :String
){
    constructor(): this("",0.0,null,"null","")
}