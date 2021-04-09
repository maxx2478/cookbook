package com.manohar.cookbook.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.manohar.cookbook.R
import com.manohar.cookbook.authentication.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val actionBar = supportActionBar
        actionBar!!.hide()

        val lottieAnimationView: LottieAnimationView = findViewById(R.id.animationView)
        lottieAnimationView.playAnimation ()

        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser!=null)
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            else
            {
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))

            }
           finish()
        }

    }
}