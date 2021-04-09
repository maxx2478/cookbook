package com.manohar.cookbook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.manohar.cookbook.R
import com.manohar.cookbook.models.CommentsModel
import com.manohar.cookbook.models.CuisineModel
import java.util.ArrayList

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.CountryViewHolder>
{

    var context: Context?=null
    var commentslist: ArrayList<CommentsModel>?=null
    constructor(commentslist: ArrayList<CommentsModel>, context: Context): super()
    {
        this.context = context
        this.commentslist = commentslist

    }

    fun updateData(commentslist: ArrayList<CommentsModel>)
    {
        this.commentslist = commentslist
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        return CountryViewHolder(
            LayoutInflater.from(parent.context)!!.inflate(R.layout.item_comment, parent, false))
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val commentsModelx: CommentsModel = commentslist?.get(position)!!
        holder.bind(commentsModelx)

    }

    override fun getItemCount(): Int {
       return commentslist!!.size
    }

    class CountryViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val name = view.findViewById<TextView>(R.id.name)
        val comment = view.findViewById<TextView>(R.id.comment)

        fun bind(commentsModelx: CommentsModel)
        {
            name.text = commentsModelx.name
            comment.text = commentsModelx.comment

        }
    }



}