package com.test.studo.adapters

import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.test.studo.R
import com.test.studo.api.models.CompactAd
import com.test.studo.ui.AdsPageFragment
import kotlinx.android.synthetic.main.view_item_recyclerview_ads.view.*


class AdsRecyclerViewAdapter(private var adList: List<CompactAd>, adsPageFragment: AdsPageFragment) : RecyclerView.Adapter<AdsRecyclerViewAdapter.CardViewViewHolder>() {

    override fun getItemCount() = adList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) : CardViewViewHolder{
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.view_item_recyclerview_ads, viewGroup, false)

        view.setOnClickListener(onRecyclerViewClickListener)

        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(cardViewViewHolder: CardViewViewHolder, i: Int) {
        cardViewViewHolder.name.text = adList[i].name
        cardViewViewHolder.description.text = adList[i].shortDescription
        //cardViewViewHolder.icon.setImageResource(adList[i].iconId)
    }

    inner class CardViewViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.name
        var description: TextView = itemView.description
        var icon: ImageView = itemView.icon
    }

    private val onRecyclerViewClickListener = View.OnClickListener{
        val rv = it.parent as RecyclerView
        val item = adList[rv.getChildLayoutPosition(it)]

        adsPageFragment.onAdClick(item)
    }

}