package com.test.studo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.view_item_listview.view.*

class ListViewItemModel(val title : String, val icon : Int )

class ListViewAdapter(var ctx : Context , var resource : Int, var items : List<ListViewItemModel>)
    : ArrayAdapter<ListViewItemModel>( ctx , resource , items ){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflater :LayoutInflater = LayoutInflater.from(context)

        val view : View = layoutInflater.inflate(resource , null )
        val imageView = view.icon
        val textView = view.title

        val person : ListViewItemModel = items[position]

        imageView.setImageDrawable(context.resources.getDrawable(person.icon))
        textView.text = person.title

        return view
    }

}