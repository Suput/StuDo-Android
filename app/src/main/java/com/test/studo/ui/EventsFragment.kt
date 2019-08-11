package com.test.studo.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.studo.R
import kotlinx.android.synthetic.main.fragment_events.view.*
import android.content.Intent

class EventsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_events, container, false)

        view.btn.setOnClickListener{
            this.activity?.getSharedPreferences("shared", Context.MODE_PRIVATE)?.edit()?.clear()?.apply()

            startActivity(Intent(context, MainActivity::class.java))
            activity?.finish()
        }

        return view
    }
}
