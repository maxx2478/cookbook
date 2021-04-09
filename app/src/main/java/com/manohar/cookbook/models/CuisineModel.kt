package com.manohar.cookbook.models

data class CuisineModel(
     val name:String,
     val image:String,
     val desc:String
)
{
    constructor() : this("", "", "")
    {

    }
}

