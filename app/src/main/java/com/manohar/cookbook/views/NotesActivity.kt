package com.manohar.cookbook.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.paging.FirebaseDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.manohar.cookbook.R
import com.manohar.cookbook.adapters.CuisineAdapter
import com.manohar.cookbook.adapters.NotesAdapter
import com.manohar.cookbook.models.CuisineModel
import com.manohar.cookbook.models.NotesModel

class NotesActivity : AppCompatActivity() {

    lateinit var notesAdapter: NotesAdapter
    private var recyclerView: RecyclerView?=null
    private var notesModel: NotesModel?=null
    var noteslist:ArrayList<NotesModel>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        val actionBar = supportActionBar
        actionBar!!.hide()

        recyclerView = findViewById(R.id.notesrv)
        noteslist = arrayListOf()
        notesAdapter = NotesAdapter(noteslist!!, this)

        val ref = intent.getStringExtra("ref")
        val s = ref!!.replace("\\s".toRegex(), "")

        val linearLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.adapter = notesAdapter

        val dbref = FirebaseDatabase.getInstance().getReference("notes").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(s)
        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (getsnapshot: DataSnapshot in snapshot.children) {
                        val notesmodel = getsnapshot.getValue(NotesModel::class.java)
                        noteslist!!.add(notesmodel!!)
                        notesAdapter.updateData(noteslist!!)

                    }
                } else {
                    Toast.makeText(this@NotesActivity, "No notes found :( ", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }
}