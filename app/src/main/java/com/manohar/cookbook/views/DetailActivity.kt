package com.manohar.cookbook.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.manohar.cookbook.R
import com.manohar.cookbook.adapters.CommentsAdapter
import com.manohar.cookbook.models.BookmarksModel
import com.manohar.cookbook.models.CommentsModel
import com.manohar.cookbook.models.NotesModel
import com.manohar.cookbook.utils.PreferenceHelper
import com.manohar.cookbook.utils.PreferenceHelper.set
import com.manohar.cookbook.utils.getProgressDrawable
import com.manohar.cookbook.utils.loadimage
import java.util.*
import kotlin.collections.ArrayList

class DetailActivity : AppCompatActivity() {

    private var name: TextView?=null
    private var time: TextView?=null
    private var desc: TextView?=null
    private var image: ImageView?=null
    private var bookmark: TextView?=null
    private var preferences:SharedPreferences?=null
   // private var notice: TextView?=null
    private var addcomment: TextInputEditText?=null
    private var postcomment: MaterialButton?=null
    private var addnote: MaterialButton?=null
    private var viewnotes: MaterialButton?=null

    private var emailname: String?=null

    lateinit var commentsAdapter: CommentsAdapter
    private var commentslist:ArrayList<CommentsModel>?=null
     var recyclerview: RecyclerView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val actionBar = supportActionBar
        actionBar!!.hide()
        initializeView()

        commentslist = ArrayList()
        commentsAdapter = CommentsAdapter(commentslist!!, this)
        val linearLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerview!!.layoutManager = linearLayoutManager
        recyclerview!!.adapter = commentsAdapter

        val helper = PreferenceHelper.defaultPrefs(this)
        if (helper.getBoolean("emaillogin", false))
        {
            val getref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            getref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                        emailname = snapshot.child("name").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }


        val namex = intent.getStringExtra("name").toString()
        val timex = intent.getStringExtra("time").toString()
        val descx = intent.getStringExtra("desc").toString()
        val imagex = intent.getStringExtra("image").toString()
        setData(namex, timex, descx, imagex)

        bookmark!!.setOnClickListener(View.OnClickListener {
            val bookmarksModel = BookmarksModel(namex, imagex, descx, timex)
            addtoBookmark(bookmarksModel)
        })



        checkStatus(namex)
        getComments(namex)
        onclickListeners(namex)

    }

    private fun onclickListeners(namex: String) {

        val helper = PreferenceHelper.defaultPrefs(this)
        if (helper.getBoolean("loggedin", false))
        {


            postcomment!!.setOnClickListener(View.OnClickListener {

                if (helper.getBoolean("emaillogin", false)) {
                    val comment = addcomment!!.text.toString()
                    if (!comment.isEmpty()) {
                        val s = namex.replace("\\s".toRegex(), "")
                        val refx = FirebaseDatabase.getInstance().getReference("comments").child(s.toLowerCase(Locale.ROOT))
                        val commentsModel = CommentsModel(emailname!!, comment)
                        refx.push().setValue(commentsModel)
                        Toast.makeText(this, "Added comment", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Please enter something first", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val comment = addcomment!!.text.toString()
                    if (!comment.isEmpty()) {
                        val s = namex.replace("\\s".toRegex(), "")
                        val refx = FirebaseDatabase.getInstance().getReference("comments").child(s.toLowerCase(Locale.ROOT))
                        val commentsModel = CommentsModel(FirebaseAuth.getInstance().currentUser!!.displayName!!, comment)
                        refx.push().setValue(commentsModel)
                        Toast.makeText(this, "Added comment", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Please enter something first", Toast.LENGTH_SHORT).show()
                    }
                }


            }
            )
        }
        else
        {
            Toast.makeText(this, "Sign in to post comment", Toast.LENGTH_SHORT).show()
        }

        addnote!!.setOnClickListener(View.OnClickListener {
            val helperclass = PreferenceHelper.defaultPrefs(this)

            if (helper.getBoolean("loggedin", false)) {
                val factory = LayoutInflater.from(this)
                val deleteDialogView: View = factory.inflate(R.layout.add_note_dialog, null)
                val deleteDialog = AlertDialog.Builder(this).create()
                deleteDialog.setView(deleteDialogView)

                val text = deleteDialogView.findViewById<EditText>(R.id.setnote)
                deleteDialogView.findViewById<View>(R.id.addnotetodb).setOnClickListener {

                    if (text.text.length > 0) {
                        val note = text.text.toString()
                        if (helperclass.getBoolean("emaillogin", false)) {
                            val notesModel = NotesModel(note, emailname!!)
                            val s = namex.replace("\\s".toRegex(), "")
                            val databaseReference = FirebaseDatabase.getInstance().getReference("notes")
                                    .child(FirebaseAuth.getInstance().currentUser!!.getUid())
                                    .child(s)

                            databaseReference.push().setValue(notesModel)
                            deleteDialog.cancel()
                            Toast.makeText(this, "Note has been added", Toast.LENGTH_SHORT).show()
                        } else {
                            val notesModel = NotesModel(note, FirebaseAuth.getInstance().currentUser!!.displayName!!)
                            val s = namex.replace("\\s".toRegex(), "")

                            val databaseReference = FirebaseDatabase.getInstance().getReference("notes")
                                    .child(FirebaseAuth.getInstance().currentUser!!.getUid())
                                    .child(s)

                            databaseReference.push().setValue(notesModel)
                            deleteDialog.cancel()
                            Toast.makeText(this, "Note has been added", Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        Toast.makeText(this, "Note must not be empty", Toast.LENGTH_SHORT).show()
                    }


                }
                deleteDialog.show()

            } else {
                Toast.makeText(this, "Sign in to add notes!", Toast.LENGTH_SHORT).show()
            }


        })


        viewnotes!!.setOnClickListener(View.OnClickListener {
            val s = namex.replace("\\s".toRegex(), "")
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("ref", s)
            startActivity(intent)
        })

    }

    private fun checkStatus(namex: String) {
        if (preferences!!.getBoolean(namex, false))
        {
            bookmark!!.text = "Saved"
            bookmark!!.isClickable = false
        }

    }


    private fun setData(name: String, time: String, desc: String, image: String) {
        this.name!!.text = name
        this.time!!.text = "Time Required: " + time
        this.desc!!.text = "Instructions: \n" + desc
        val progressDrawable = getProgressDrawable(this)
        this.image!!.loadimage(image, progressDrawable)



    }

    private fun initializeView() {
        name = findViewById(R.id.name)
        time = findViewById(R.id.time)
        desc = findViewById(R.id.desc)
        image = findViewById(R.id.image)
        bookmark = findViewById(R.id.bookmarkit)
        preferences = PreferenceHelper.defaultPrefs(this)
        recyclerview = findViewById(R.id.commentsrv)
        //notice = findViewById(R.id.notice)
        addcomment = findViewById(R.id.addcomment)
        postcomment = findViewById(R.id.post)
        addnote = findViewById(R.id.addnotes)
        viewnotes = findViewById(R.id.viewnote)

    }

    fun getComments(ref: String)
    {

        val s = ref.replace("\\s".toRegex(), "")
        val refx = FirebaseDatabase.getInstance().getReference("comments").child(s.toLowerCase(Locale.ROOT))
        refx.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    commentslist!!.clear()

                    for (getSnapshot: DataSnapshot in snapshot.children) {
                        val commentsModelx: CommentsModel? = getSnapshot.getValue(CommentsModel::class.java)
                        commentslist!!.add(commentsModelx!!)
                        commentsAdapter.updateData(commentslist!!)

                    }
                } else {
                    //notice!!.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }



    fun addtoBookmark(bookmarksModel: BookmarksModel) {

        val helper = PreferenceHelper.defaultPrefs(this)

        if (helper.getBoolean("loggedin", false))
        {
            if (!preferences!!.getBoolean(bookmarksModel.name, false))
            {
                preferences!!.set(bookmarksModel.name, true)
                val ref = FirebaseDatabase.getInstance().getReference("bookmarks").child(FirebaseAuth.getInstance().currentUser!!.uid).push()
                ref.setValue(bookmarksModel)
                Toast.makeText(this, "Added to your bookmarks", Toast.LENGTH_SHORT).show()
                bookmark!!.text = "Saved"
            }
            else
            {
                Toast.makeText(this, "Already added to your bookmarks", Toast.LENGTH_SHORT).show()
            }

        }
        else
        {
            Toast.makeText(this, "Sign in to save recipe.", Toast.LENGTH_SHORT).show()
        }




    }
}