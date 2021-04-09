package com.manohar.cookbook.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.manohar.cookbook.views.MainActivity
import com.manohar.cookbook.R
import com.manohar.cookbook.utils.PreferenceHelper
import com.manohar.cookbook.utils.PreferenceHelper.set

class LoginActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var auth: FirebaseAuth
    private var button:MaterialButton?=null
    private var email: TextInputEditText?=null
    private var password:TextInputEditText?=null
    var sharedPreferences:SharedPreferences?=null
    var temp:Boolean= true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val actionBar = supportActionBar
        actionBar!!.hide()
        sharedPreferences = PreferenceHelper.defaultPrefs(this)

        button = findViewById(R.id.login)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        button!!.setOnClickListener(View.OnClickListener {
            emailSignIn(email = email!!.text.toString(), password = password!!.text.toString())
        })


        auth = FirebaseAuth.getInstance()
        configureGoogleSignIn()
    }

    fun signupActivity(view: View) {
        startActivity(Intent(this, SignupActivity::class.java))
    }

    fun emailSignIn(email: String, password: String)
    {
        if (checkEmailErrors(email, password))
        {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        sharedPreferences!!.set("loggedin", true)

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Incorrect credentials !", Toast.LENGTH_SHORT).show()

                    }
                }
        }

    }

    fun googleLogin(view: View) {
        signInGoogle()
    }

    fun signInGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, 222)
    }

    fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 222) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Success", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Failed", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    if (user!=null)
                    {
                        FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                            .child("uid").setValue(user.uid)
                        FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                            .child("name").setValue(user.displayName)
                        FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                            .child("email").setValue(user.email)
                    }
                    sharedPreferences!!.set("loggedin", true)

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                    //updateUI(user)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "An error occured, please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    fun skipLogin(view: View) {

        auth.signInAnonymously().addOnCompleteListener(this,
            OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    sharedPreferences!!.set("loggedin", false)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                else {
                    Toast.makeText(
                        this@LoginActivity,
                        "An error occured, please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })



    }

    fun checkEmailErrors(email: String, password: String):Boolean
    {

        //To check , if the email and password are empty.
        if (email != "" && password != "") {
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


}
