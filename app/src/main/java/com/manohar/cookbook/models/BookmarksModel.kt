package com.manohar.cookbook.models

data class BookmarksModel(
        val name:String,
        val image:String,
        val instructions:String,
        val time:String
)
{
    constructor() : this("","","","") {

    }

}
