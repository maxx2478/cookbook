package com.manohar.cookbook.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.manohar.cookbook.R
import com.manohar.cookbook.authentication.LoginActivity
import com.manohar.cookbook.utils.PreferenceHelper


class AboutFragment : Fragment() {

    var root:View?=null
    var notice: TextView?=null
    var signin:MaterialButton?=null
    var signout:MaterialButton?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_about, container, false)

        val helper = PreferenceHelper.defaultPrefs(requireContext())
        notice = root!!.findViewById(R.id.notice)
        signin = root!!.findViewById(R.id.siggin)
        signout = root!!.findViewById(R.id.signout)

        if (helper.getBoolean("loggedin", false))
        {
            signout!!.visibility = View.VISIBLE
        }
        else
        {
            notice!!.visibility = View.VISIBLE
            signin!!.visibility = View.VISIBLE

        }


        signin!!.setOnClickListener(View.OnClickListener {
            helper.edit().clear().apply()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()

        })

        signout!!.setOnClickListener(View.OnClickListener {
            helper.edit().clear().apply()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()

        })

        return root
    }


}