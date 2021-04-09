package com.manohar.cookbook.views.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.manohar.cookbook.R
import com.manohar.cookbook.adapters.CuisineAdapter
import com.manohar.cookbook.models.CuisineModel
import com.manohar.cookbook.utils.PreferenceHelper
import com.manohar.cookbook.views.AddRecipe
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {


    var root:View?=null
    lateinit var cuisineAdapter: CuisineAdapter
    private var recyclerView:RecyclerView?=null
    private var cuisineModel:CuisineModel?=null
    var cuisinelist:ArrayList<CuisineModel>?=null
    var progressBar:ProgressBar?=null
    var dataIsReady:Boolean = false
    var searchbar:SearchView?=null
    var timer: Timer?=null
    var searchData:ArrayList<CuisineModel>?=null
    var addrecipebutton: FloatingActionButton?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root =  inflater.inflate(R.layout.fragment_home, container, false)
        cuisinelist = ArrayList()
        searchData = ArrayList()

        initializeViews(root!!)
        cuisineAdapter = CuisineAdapter(cuisinelist!!, requireContext())
        recyclerView!!.adapter = cuisineAdapter

        loadDefaultdata()
        keepCheck()
        clickListeners()

        return root

    }

    private fun clickListeners() {
        searchbar!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.length>0)
                    SearchQuery(newText.toLowerCase(Locale.ROOT))
                else
                {
                    restoreData()
                }

                return false
            }

        })


        //Listen to searchview's close/clear button
        val closeButton = searchbar!!.findViewById<View>(R.id.search_close_btn) as ImageView
        closeButton.setOnClickListener {
            searchbar!!.setQuery("", false)
            restoreData()

        }

        addrecipebutton!!.setOnClickListener(View.OnClickListener {
            val helper = PreferenceHelper.defaultPrefs(requireContext())

            if (helper.getBoolean("loggedin", false))
            {
                startActivity(Intent(requireContext(), AddRecipe::class.java))
            }
            else
            {
                Toast.makeText(requireContext(), "Sign in to submit your Recipe", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun SearchQuery(query: String?) {
        searchData!!.clear()
        val classref = FirebaseDatabase.getInstance().getReference("cuisine")
        classref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (getsnapshot: DataSnapshot in snapshot.children) {
                        cuisineModel = getsnapshot.getValue(CuisineModel::class.java)
                        if(cuisineModel!!.name.contains(query!!, ignoreCase = true))
                        {
                            searchData!!.add(cuisineModel!!)
                            cuisineAdapter.updateData(searchData!!)
                            dataIsReady = true
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun initializeViews(root: View)
    {
        recyclerView = root.findViewById(R.id.homerv)
        searchbar = root.findViewById(R.id.search_bar)
        progressBar = root.findViewById(R.id.progress)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
        addrecipebutton = root.findViewById(R.id.addrecipe)

    }

    fun restoreData()
    {
        cuisineAdapter.updateData(cuisinelist!!)
    }

    private fun loadDefaultdata() {
        val classref = FirebaseDatabase.getInstance().getReference("cuisine")
        classref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (getsnapshot: DataSnapshot in snapshot.children) {
                        cuisineModel = getsnapshot.getValue(CuisineModel::class.java)
                        cuisinelist!!.add(cuisineModel!!)
                        cuisineAdapter.notifyDataSetChanged()
                        dataIsReady = true
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }


    fun keepCheck() {
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            //Running on BG Thread
            override fun run() {
                if (dataIsReady) {
                    timer!!.cancel()
                    Handler(Looper.getMainLooper()).post {
                        progressBar!!.visibility = View.GONE
                    }
                }
            }
        }, 0, 600)
    }



}