package com.manohar.cookbook.views

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.manohar.cookbook.R
import com.manohar.cookbook.adapters.CuisineAdapter
import com.manohar.cookbook.adapters.RecipeAdapter
import com.manohar.cookbook.models.CuisineModel
import com.manohar.cookbook.models.RecipiModel
import com.manohar.cookbook.utils.PreferenceHelper
import com.manohar.cookbook.utils.PreferenceHelper.set
import java.util.*
import kotlin.collections.ArrayList

class RecipeActivity : AppCompatActivity() {

    lateinit var recipeAdapter: RecipeAdapter
    private var recyclerView: RecyclerView?=null
    private var recipiModel: RecipiModel?=null
    var recipelist:ArrayList<RecipiModel>?=null
    var progressBar: ProgressBar?=null
    var searchData:ArrayList<RecipiModel>?=null
    var dataIsReady:Boolean = false
    var timer: Timer?=null
    var preferencesheler:SharedPreferences?=null
    var searchbar: SearchView?=null
    var notex: TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val actionBar = supportActionBar
        actionBar!!.hide()

        preferencesheler = com.manohar.cookbook.utils.PreferenceHelper.defaultPrefs(this)
        initializeViews()
        recipelist = arrayListOf()
        searchData = arrayListOf()

        val ref:String = intent.getStringExtra("ref").toString()
        recipeAdapter = RecipeAdapter(recipelist!!, this)
        recyclerView!!.adapter = recipeAdapter
        preferencesheler!!.set("ref", ref.toLowerCase()) // to use to restore the data
        loadDefaultdata(ref.toLowerCase())
        keepCheck()
        clickListeners()

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
                    restoreData()
                return false
            }

        })


        //Listen to searchview's close/clear button
        val closeButton = searchbar!!.findViewById<View>(R.id.search_close_btn) as ImageView
        closeButton.setOnClickListener {
            searchbar!!.setQuery("", false)
            restoreData()

        }

    }

    private fun initializeViews() {

        recyclerView = findViewById(R.id.reciperv)
        searchbar = findViewById(R.id.search_bar)
        notex = findViewById(R.id.notex)
        progressBar = findViewById(R.id.progress)
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
    }

    private fun SearchQuery(query: String?) {
        searchData!!.clear()
        val classref = FirebaseDatabase.getInstance().getReference(preferencesheler!!.getString("ref","")!!)
        classref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (getsnapshot: DataSnapshot in snapshot.children) {
                        recipiModel = getsnapshot.getValue(RecipiModel::class.java)
                        if(recipiModel!!.name.contains(query!!, ignoreCase = true))
                        {
                            searchData!!.add(recipiModel!!)
                            recipeAdapter.updateData(searchData!!)
                            recipeAdapter.notifyDataSetChanged()
                            dataIsReady = true
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    fun restoreData()
    {
        recipeAdapter.updateData(recipelist!!)
    }

    private fun loadDefaultdata(ref:String) {
        recipelist!!.clear()
        val classref = FirebaseDatabase.getInstance().getReference(ref)
        classref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (getsnapshot: DataSnapshot in snapshot.children) {
                        recipiModel = getsnapshot.getValue(RecipiModel::class.java)
                        recipelist!!.add(recipiModel!!)
                        recipeAdapter.addRef(getsnapshot.key!!)
                        recipeAdapter.notifyDataSetChanged()
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
                        notex!!.visibility = View.GONE

                    }
                }
                else
                {
                    if (recipeAdapter.itemCount<=0)
                    {
                        progressBar!!.visibility = View.GONE
                        notex!!.visibility = View.VISIBLE
                    }
                }
            }
        }, 0, 500)
    }

}