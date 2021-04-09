package com.manohar.cookbook.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.manohar.cookbook.views.MainActivity
import com.manohar.cookbook.R

class SignupActivity : AppCompatActivity() {


    private var name: TextInputEditText?=null
    private var email:TextInputEditText?=null
    private var password:TextInputEditText?=null
    private var signup:MaterialButton?=null
    private var mAuth:FirebaseAuth?=null
    var temp:Boolean= true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val actionBar = supportActionBar
        actionBar!!.hide()

        initializeViews()
        mAuth = FirebaseAuth.getInstance()
        signup!!.setOnClickListener(View.OnClickListener {
            val name: String = name!!.text.toString()
            val email: String = email!!.text.toString()
            val password: String = password!!.text.toString()

            if (checkEmailErrors(name, email, password)) {
                EmailAndPasswordLogin(name, email, password)
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun initializeViews() {
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        signup = findViewById(R.id.signup)

    }

    fun checkEmailErrors(name: String, email: String, password: String):Boolean
    {

        //To check , if the email and password are empty.
        if (email != "" && password != "" && name != "") {
            if (!email.endsWith("@gmail.com")) {
                temp = false
                Snackbar.make(
                    this.findViewById(android.R.id.content),
                    "Email address is not valid",
                    Snackbar.LENGTH_LONG
                ).show()

            }
            else if (password.length<=6)
            {
                temp = false
                Snackbar.make(
                    this.findViewById(android.R.id.content),
                    "Password must be greater than 6 characters",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            temp = false
        }
        return temp

    }

    private fun EmailAndPasswordLogin(username:String, Email: String, Password: String) {

                mAuth!!.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@SignupActivity,
                                "Registered Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val user: FirebaseUser = mAuth!!.currentUser!!
                            val uid = user.uid
                            FirebaseDatabase.getInstance().getReference("users").child(uid)
                                .child("uid").setValue(uid)
                            FirebaseDatabase.getInstance().getReference("users").child(uid)
                                .child("name").setValue(username)
                            FirebaseDatabase.getInstance().getReference("users").child(uid)
                                .child("password").setValue(Password)
                            FirebaseDatabase.getInstance().getReference("users").child(uid)
                                .child("email").setValue(Email)

                            startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                            finish()
                        }
                    }).addOnFailureListener(OnFailureListener { e ->
                    Toast.makeText(this@SignupActivity, e.message, Toast.LENGTH_SHORT).show()
                    Toast.makeText(
                        this@SignupActivity,
                        "Please Set Up An Unique Username",
                        Toast.LENGTH_SHORT
                    ).show()
                })


    }


}