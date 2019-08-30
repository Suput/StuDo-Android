package com.test.studo.adapters

import android.widget.TextView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.test.studo.R
import com.test.studo.api.models.Organization
import com.test.studo.ui.AdsPageFragment
import com.test.studo.ui.OrganizationsPageFragment
import kotlinx.android.synthetic.main.view_item_recyclerview_ads.view.*


class OrganizationsRecyclerViewAdapter(private var organizationsList: List<Organization>, organizationsPageFragment: OrganizationsPageFragment) : RecyclerView.Adapter<OrganizationsRecyclerViewAdapter.CardViewViewHolder>() {

    override fun getItemCount() = organizationsList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) : CardViewViewHolder{
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.view_item_recyclerview_ads, viewGroup, false)

        view.setOnClickListener(onRecyclerViewClickListener)

        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(cardViewViewHolder: CardViewViewHolder, i: Int) {
        cardViewViewHolder.name.text = organizationsList[i].name
        cardViewViewHolder.description.text = organizationsList[i].description
        //cardViewViewHolder.icon.setImageResource(organizationsList[i].iconId)
    }

    inner class CardViewViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.name
        var description: TextView = itemView.description
        var icon: ImageView = itemView.icon
    }

    private val onRecyclerViewClickListener = View.OnClickListener{
        val rv = it.parent as RecyclerView
        val item = organizationsList[rv.getChildLayoutPosition(it)]

        //organizationsPageFragment.onOrganizationClick(it.ad_panel, item)
    }

}