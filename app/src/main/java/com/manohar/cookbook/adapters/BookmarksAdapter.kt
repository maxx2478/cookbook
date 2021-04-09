package com.manohar.cookbook.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.manohar.cookbook.R
import com.manohar.cookbook.models.BookmarksModel
import com.manohar.cookbook.utils.getProgressDrawable
import com.manohar.cookbook.utils.loadimage
import com.manohar.cookbook.views.DetailActivity
import java.util.ArrayList

class BookmarksAdapter : RecyclerView.Adapter<BookmarksAdapter.BookmarksHolder>
{

    var context: Context?=null
    var bookmarkslist: ArrayList<BookmarksModel>?=null
    constructor(bookmarkslist: ArrayList<BookmarksModel>, context: Context): super()
    {
        this.context = context
        this.bookmarkslist = bookmarkslist

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarksHolder {
        return BookmarksHolder(
                LayoutInflater.from(parent.context)!!.inflate(R.layout.item_cuisine_and_recipe, parent, false))
    }

    override fun onBindViewHolder(holder: BookmarksHolder, position: Int) {
        val bookmarksModel: BookmarksModel = bookmarkslist?.get(position)!!
        holder.bind(bookmarksModel)
        holder.itemView.setOnClickListener(View.OnClickListener {
            val i = Intent(holder.itemView.context, DetailActivity::class.java)
            i.putExtra("name", bookmarksModel.name)
            i.putExtra("time", bookmarksModel.time)
            i.putExtra("desc", bookmarksModel.instructions)
            i.putExtra("image", bookmarksModel.image)

            holder.itemView.context.startActivity(i)
        })
    }

    override fun getItemCount(): Int {
        return bookmarkslist!!.size
    }

    class BookmarksHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val name = view.findViewById<TextView>(R.id.name)
        val desc = view.findViewById<TextView>(R.id.desc)

        val image = view.findViewById<ImageView>(R.id.cuisineimage)
        private val progressDrawable = getProgressDrawable(view.context)

        fun bind(bookmarksModel: BookmarksModel)
        {
            name.text = bookmarksModel.name
            desc.text = bookmarksModel.instructions

            image.loadimage(bookmarksModel.image, progressDrawable)
        }
    }



}