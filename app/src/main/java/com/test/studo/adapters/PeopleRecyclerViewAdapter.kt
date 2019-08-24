package com.test.studo.adapters

import android.widget.TextView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.test.studo.R
import com.test.studo.api.models.CompactResume
import kotlinx.android.synthetic.main.view_item_recyclerview.view.*


class PeopleRecyclerViewAdapter(private var compactResumeList: List<CompactResume>) : RecyclerView.Adapter<PeopleRecyclerViewAdapter.CardViewViewHolder>() {

    override fun getItemCount() = compactResumeList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) : CardViewViewHolder{
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.view_item_recyclerview, viewGroup, false)

        view.setOnClickListener(onRecyclerViewClickListener)

        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(cardViewViewHolder: CardViewViewHolder, i: Int) {
        cardViewViewHolder.name.text = compactResumeList[i].name
        cardViewViewHolder.description.text = compactResumeList[i].description
        cardViewViewHolder.icon.setImageResource(R.drawable.ic_account_circle_black_24dp)
    }

    inner class CardViewViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cv: CardView = itemView.cv
        var name: TextView = itemView.name
        var description: TextView = itemView.description
        var icon: ImageView = itemView.icon
    }

    private val onRecyclerViewClickListener = View.OnClickListener{
        val rv = it.parent as RecyclerView
        val item = compactResumeList[rv.getChildLayoutPosition(it)]
        Log.wtf("body", item.name)
    }

}