package com.manohar.cookbook.models

data class RecipiModel(
    val name:String,
    val image:String,
    val instructions:String,
    val time:String

)

{

    constructor() : this("","","", "") {

    }

}
