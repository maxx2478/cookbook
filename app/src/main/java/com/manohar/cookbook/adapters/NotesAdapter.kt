package com.manohar.cookbook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.manohar.cookbook.R
import com.manohar.cookbook.models.NotesModel
import java.util.ArrayList

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NotesViewholder>
{

    var context: Context?=null
    var noteslist: ArrayList<NotesModel>?=null
    constructor(cuisinelist: ArrayList<NotesModel>, context: Context)
    {
        this.context = context
        this.noteslist = cuisinelist

    }

    fun updateData(cuisinelist: ArrayList<NotesModel>)
    {
        this.noteslist = cuisinelist
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewholder {
        return NotesViewholder(
                LayoutInflater.from(parent.context)!!.inflate(
                        R.layout.item_note,
                        parent,
                        false
                )
        )
    }


    override fun onBindViewHolder(holder: NotesViewholder, position: Int) {
        val cuisinemodel = noteslist?.get(position)
        holder.bind(cuisinemodel!!)

    }

    override fun getItemCount(): Int {
        return noteslist!!.size
    }


    class NotesViewholder(view: View): RecyclerView.ViewHolder(view)
    {
        val note = view.findViewById<TextView>(R.id.notes)


        fun bind(cuisinemodel: NotesModel)
        {
            note.text = cuisinemodel.note

        }
    }



}