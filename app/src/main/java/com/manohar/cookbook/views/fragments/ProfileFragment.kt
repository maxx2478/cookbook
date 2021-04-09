package com.manohar.cookbook.views.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.manohar.cookbook.R
import com.manohar.cookbook.adapters.BookmarksAdapter
import com.manohar.cookbook.adapters.CuisineAdapter
import com.manohar.cookbook.models.BookmarksModel
import com.manohar.cookbook.models.CuisineModel
import com.manohar.cookbook.utils.PreferenceHelper
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {


    var root:View?=null
    lateinit var adapterx: BookmarksAdapter
    private var recyclerView: RecyclerView?=null
    private var bookmarksModel: BookmarksModel?=null
    var bookmarklist:ArrayList<BookmarksModel>?=null
    var progressBar: ProgressBar?=null
    var dataIsReady:Boolean = false
    var notex:TextView?=null
    var username:TextView?=null

    var timer: Timer?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root =  inflater.inflate(R.layout.fragment_profile, container, false)

        initializeViews(root!!)
        bookmarklist = arrayListOf()
        adapterx = BookmarksAdapter(bookmarklist!!, requireContext())
        recyclerView!!.adapter = adapterx

        setname()
        loadDefaultdata()
        keepCheck()

        return root
    }

    fun initializeViews(root: View)
    {
        recyclerView = root.findViewById(R.id.bookmarksrv)
        progressBar = root.findViewById(R.id.progress)
        notex = root.findViewById(R.id.notex)
        username = root.findViewById(R.id.username)
        val linearLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager

    }

    fun setname()
    {
        val helper = PreferenceHelper.defaultPrefs(requireContext())
        if (helper.getBoolean("loggedin", false))
        {

            if (helper.getBoolean("emaillogin", false))
            {
                val getref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                getref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists())
                        {
                           val emailname = snapshot.child("name").value.toString()
                            username!!.text = "Hi " + emailname
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
            else
            {
                username!!.text = "Hi " +  FirebaseAuth.getInstance().currentUser!!.displayName
            }
        }
        else
        {
            username!!.text = "Hello Fooody"
        }

    }


    private fun loadDefaultdata() {
        val classref = FirebaseDatabase.getInstance().getReference("bookmarks").child(FirebaseAuth.getInstance().currentUser!!.uid)
        classref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    bookmarklist!!.clear()
                    for (getsnapshot: DataSnapshot in snapshot.children) {
                        bookmarksModel = getsnapshot.getValue(BookmarksModel::class.java)
                        bookmarklist!!.add(bookmarksModel!!)
                        adapterx.notifyDataSetChanged()
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
                    if (adapterx.itemCount<=0)
                    {
                        progressBar!!.visibility = View.GONE
                        notex!!.visibility = View.VISIBLE
                    }
                }
            }
        }, 0, 2000)
    }

}