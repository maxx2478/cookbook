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
import com.manohar.cookbook.models.CuisineModel
import com.manohar.cookbook.models.RecipiModel
import com.manohar.cookbook.utils.getProgressDrawable
import com.manohar.cookbook.utils.loadimage
import com.manohar.cookbook.views.DetailActivity
import com.manohar.cookbook.views.RecipeActivity
import java.util.ArrayList

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewholder>
{

    var context: Context?=null
    var recipilist: List<RecipiModel>?=null
    var recipilistref: ArrayList<String>?=null

    constructor(recipilist: ArrayList<RecipiModel>, context: Context): super()
    {
        this.context = context
        this.recipilist = recipilist
        recipilistref = arrayListOf()
    }

    fun updateData(recipilist: List<RecipiModel>)
    {
        this.recipilist = recipilist
        notifyDataSetChanged()
    }

    fun addRef(ref:String)
    {
        recipilistref!!.add(ref)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewholder {
        return RecipeViewholder(
            LayoutInflater.from(parent.context)!!.inflate(R.layout.item_cuisine_and_recipe, parent, false))
    }

    override fun onBindViewHolder(holder: RecipeViewholder, position: Int) {
        val recipiModelp: RecipiModel = recipilist?.get(position)!!
        holder.bind(recipiModelp)


        holder.itemView.setOnClickListener(View.OnClickListener {
            val i = Intent(holder.itemView.context, DetailActivity::class.java)
            i.putExtra("name", recipiModelp.name)
            i.putExtra("time", recipiModelp.time)
            i.putExtra("desc", recipiModelp.instructions)
            i.putExtra("image", recipiModelp.image)

            holder.itemView.context.startActivity(i)
        })

    }

    override fun getItemCount(): Int {
        return recipilist!!.size
    }

    class RecipeViewholder(view: View): RecyclerView.ViewHolder(view)
    {
        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.cuisineimage)
        val desc = view.findViewById<TextView>(R.id.desc)

        private val progressDrawable = getProgressDrawable(view.context)

        fun bind(recipiModel : RecipiModel)
        {
            name.text = recipiModel.name
            desc.text = recipiModel.instructions

            image.loadimage(recipiModel.image, progressDrawable)
        }
    }


}