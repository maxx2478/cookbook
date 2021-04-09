package com.manohar.cookbook.models

data class NotesModel(
        val note:String,
        val noteby:String

)
{
    constructor() : this("","")
    {

    }
}
