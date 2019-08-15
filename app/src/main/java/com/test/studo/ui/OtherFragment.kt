package com.test.studo.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.test.studo.R
import kotlinx.android.synthetic.main.fragment_other.view.*


class OtherFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_other, container, false)

        view.other_list.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, resources.getStringArray(
            R.array.other))

        return view
    }
}
