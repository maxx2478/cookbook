package com.manohar.cookbook.models

data class CommentsModel(
    val name:String,
    val comment:String

)

{
    constructor() : this("","") {

    }

}

