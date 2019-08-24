package com.test.studo.adapters

import android.widget.TextView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.test.studo.R
import com.test.studo.api.models.CompactAd
import com.test.studo.ui.EventsPageFragment
import kotlinx.android.synthetic.main.view_item_recyclerview_events.view.*


class EventsRecyclerViewAdapter(private var compactAdList: List<CompactAd>, eventsPageFragment: EventsPageFragment) : RecyclerView.Adapter<EventsRecyclerViewAdapter.CardViewViewHolder>() {

    override fun getItemCount() = compactAdList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) : CardViewViewHolder{
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.view_item_recyclerview_events, viewGroup, false)

        view.setOnClickListener(onRecyclerViewClickListener)

        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(cardViewViewHolder: CardViewViewHolder, i: Int) {
        cardViewViewHolder.name.text = compactAdList[i].name
        cardViewViewHolder.description.text = compactAdList[i].shortDescription
        //cardViewViewHolder.icon.setImageResource(compactAdList[i].iconId)
    }

    inner class CardViewViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cv: CardView = itemView.cv
        var name: TextView = itemView.name
        var description: TextView = itemView.description
        var icon: ImageView = itemView.icon
    }

    private val onRecyclerViewClickListener = View.OnClickListener{
        val rv = it.parent as RecyclerView
        val item = compactAdList[rv.getChildLayoutPosition(it)]

        eventsPageFragment.onAdClick(it.ad_panel, item)
    }

}