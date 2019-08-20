package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.studo.R
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*

class EventsPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_events_page, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.title_events)

        return view
    }
}
