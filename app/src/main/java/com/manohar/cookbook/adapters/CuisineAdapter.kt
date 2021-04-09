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
import com.manohar.cookbook.utils.getProgressDrawable
import com.manohar.cookbook.utils.loadimage
import com.manohar.cookbook.views.RecipeActivity
import java.util.*

class CuisineAdapter :
    RecyclerView.Adapter<CuisineAdapter.CuisineViewholder>
{

    var context: Context?=null
    var cuisinelist: ArrayList<CuisineModel>?=null
    constructor(cuisinelist: ArrayList<CuisineModel>, context: Context)
    {
        this.context = context
        this.cuisinelist = cuisinelist

    }

    fun updateData(cuisinelist: ArrayList<CuisineModel>)
    {
        this.cuisinelist = cuisinelist
        notifyDataSetChanged()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineViewholder {
        return CuisineViewholder(
            LayoutInflater.from(parent.context)!!.inflate(
                R.layout.item_cuisine_and_recipe,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: CuisineViewholder, position: Int) {
        val cuisinemodel = cuisinelist?.get(position)
        holder.bind(cuisinemodel!!)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val i = Intent(holder.itemView.context, RecipeActivity::class.java)
            i.putExtra("ref", cuisinemodel.name)
            holder.itemView.context.startActivity(i)
        })

    }

    override fun getItemCount(): Int {
        return cuisinelist!!.size
    }


    class CuisineViewholder(view: View): RecyclerView.ViewHolder(view)
    {
        val name = view.findViewById<TextView>(R.id.name)
        val desc = view.findViewById<TextView>(R.id.desc)

        val image = view.findViewById<ImageView>(R.id.cuisineimage)
        private val progressDrawable = getProgressDrawable(view.context)

        fun bind(cuisinemodel: CuisineModel)
        {
            name.text = cuisinemodel.name
            desc.text = cuisinemodel.desc
            image.loadimage(cuisinemodel.image, progressDrawable)
        }
    }



}