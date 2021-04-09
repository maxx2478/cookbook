package com.manohar.cookbook.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.viewpager.widget.ViewPager
import com.fxn.BubbleTabBar
import com.fxn.OnBubbleClickListener
import com.manohar.cookbook.R
import com.manohar.cookbook.utils.NoSwipePager
import com.manohar.cookbook.utils.PreferenceHelper


class MainActivity : AppCompatActivity() {

    var bubbleTabBar: BubbleTabBar? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.hide()
        bubbleTabBar = findViewById<BubbleTabBar>(R.id.bubbleTabBar)
        bubbleTabBar!!.setSelected(0, false)

        //Initializing viewPager
        var viewPager = findViewById<View>(R.id.pager) as NoSwipePager

        //Creating our pager adapter
        val adapter = com.manohar.cookbook.adapters.PagerAdapter(
                supportFragmentManager,
                bubbleTabBar!!.childCount
        )

        //Adding adapter to pager
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3

        bubbleTabBar!!.addBubbleListener(object : OnBubbleClickListener {
            override fun onBubbleClick(i: Int) {
                when (i) {
                    R.id.home -> {
                        viewPager.currentItem = 0
                    }

                    R.id.bookmarksx -> Handler().postDelayed({
                        viewPager.currentItem = 1
                    }, 150)

                    R.id.about -> Handler().postDelayed({
                        viewPager.currentItem = 2

                    }, 150)

                }
            }
        })

        viewPager.pagingEnabled = false

    }


}