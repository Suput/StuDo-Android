package com.test.studo.adapters

import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.test.studo.R
import com.test.studo.api.models.Organization
import com.test.studo.ui.OrganizationsPageFragment
import kotlinx.android.synthetic.main.recyclerview_row_organizations.view.*


class OrganizationsRecyclerViewAdapter(private var organizationsList: List<Organization>, organizationsPageFragment: OrganizationsPageFragment) : RecyclerView.Adapter<OrganizationsRecyclerViewAdapter.CardViewViewHolder>() {

    override fun getItemCount() = organizationsList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) : CardViewViewHolder{
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recyclerview_row_organizations, viewGroup, false)

        view.setOnClickListener(onRecyclerViewClickListener)

        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(cardViewViewHolder: CardViewViewHolder, i: Int) {
        cardViewViewHolder.name.text = organizationsList[i].name
        //cardViewViewHolder.icon.setImageResource(organizationsList[i].iconId)
    }

    inner class CardViewViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.name
//        var icon: ImageView = itemView.icon
    }

    private val onRecyclerViewClickListener = View.OnClickListener{
        val rv = it.parent as RecyclerView
        val item = organizationsList[rv.getChildLayoutPosition(it)]

        organizationsPageFragment.onOrganizationClick(item)
    }

}