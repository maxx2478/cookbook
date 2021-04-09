package com.manohar.cookbook.views

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.manohar.cookbook.R
import com.manohar.cookbook.models.CuisineModel
import com.manohar.cookbook.models.RecipiModel
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class AddRecipe : AppCompatActivity() {

    var recipename:TextInputEditText?=null
    var arraySpinner:ArrayList<String>?=null
    var selectedItem:String = "indian"
    var instructions:TextInputEditText?=null
    var timetaken:TextInputEditText?=null
    var imageSelected = false
    var imagePath: Uri? = null
    var imagedownloadurl: String? = null
    var progressDialog: ProgressDialog? = null
    var placeholder: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_recipe)
        val actionBar = supportActionBar
        actionBar!!.hide()

        initializeViews()
        arraySpinner = arrayListOf()
        var adapter = ArrayAdapter(this,

                android.R.layout.simple_spinner_item, arraySpinner!!)
        val ref = FirebaseDatabase.getInstance().getReference("cuisine")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (getsnapshot: DataSnapshot in snapshot.children) {
                        val cuisineModel = getsnapshot.getValue(CuisineModel::class.java)
                        val data = cuisineModel!!.name
                        arraySpinner!!.add(data)
                        adapter.notifyDataSetChanged()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        val s = findViewById<View>(R.id.spinner) as Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s.adapter = adapter

        s.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedItem = arraySpinner!![position].toLowerCase(Locale.ROOT)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedItem = "indian"
            }

        }



    }

    private fun initializeViews() {
        recipename = findViewById(R.id.recipename)
        instructions = findViewById(R.id.instructions)
        timetaken = findViewById(R.id.timetaken)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Adding your new tasty recipe !!")
        progressDialog!!.setMessage("Please Wait..")
        progressDialog!!.setCancelable(false)
        placeholder = findViewById(R.id.placeholder)

    }

    fun submit(view: View) {

        if (recipename!!.text.toString().length<=0)
        {
            Toast.makeText(this, "Recipe name must not be empty", Toast.LENGTH_SHORT).show()
        }
        else if (instructions!!.text.toString().length<=0)
        {
            Toast.makeText(this, "instructions must not be empty", Toast.LENGTH_SHORT).show()
        }
        else if (timetaken!!.text.toString().length<=0)
        {
            Toast.makeText(this, "time taken must not be empty", Toast.LENGTH_SHORT).show()
        }
        else if (!imageSelected)
        {
            Toast.makeText(this, "please select an image", Toast.LENGTH_SHORT).show()
        }
        else
        {
            uploadImage()
        }

    }

    fun chooseImage(view: View) {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Select Your Profile Image"), 1)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imagePath = data.data
            try {
                val b = MediaStore.Images.Media.getBitmap(this.contentResolver, imagePath)
                placeholder!!.setImageBitmap(b)
                imageSelected = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun uploadImage() {
        try {
            progressDialog!!.show()
            var bitmap: Bitmap? = null
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imagePath)

            //float aspectRatio = bitmap.getWidth() /
            //        (float) bitmap.getHeight();
            // int width = 240;
            // int height = Math.round(width / aspectRatio);

            //bitmap = Bitmap.createScaledBitmap(
            //      bitmap, width, height, false);
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val data = baos.toByteArray()
            val uploader = FirebaseStorage.getInstance().reference.child("cuisine").child(selectedItem).child(Calendar.getInstance()[Calendar.SECOND].toString())
            uploader.putBytes(data).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploader.downloadUrl.addOnSuccessListener { uri ->
                        imagedownloadurl = uri.toString()
                        uploadData(imagedownloadurl!!)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun uploadData(imagedownloadurl: String) {
        val namex: String = recipename!!.text.toString()
        val timex: String = instructions!!.text.toString()
        val descx: String = timetaken!!.text.toString()

        val userModel = RecipiModel(namex,imagedownloadurl, descx , timex)
        val databaseReference = FirebaseDatabase.getInstance().getReference(selectedItem).push()
        databaseReference.setValue(userModel)
        Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
        progressDialog!!.dismiss()
        finish()
    }

}