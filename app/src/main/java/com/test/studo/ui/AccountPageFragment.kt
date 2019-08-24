package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.test.studo.R
import com.test.studo.adapters.ListViewAdapter
import com.test.studo.adapters.ListViewItemModel
import com.test.studo.currentUserWithToken
import kotlinx.android.synthetic.main.fragment_account_page.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_account_page.view.*

class AccountPageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_account_page, container, false)

        view.collapse_toolbar.title = resources.getText(R.string.title_account)

        view.name_and_surname.text = currentUserWithToken.user.firstName + " " + currentUserWithToken.user.secondName
        view.email.text = currentUserWithToken.user.email

        val list = mutableListOf<ListViewItemModel>()

        list.add(ListViewItemModel(resources.getText(R.string.my_ads).toString(), R.drawable.ic_list_black_24dp ))
        list.add(ListViewItemModel(resources.getText(R.string.subs).toString(), R.drawable.ic_star_black_24dp ))
        list.add(ListViewItemModel(resources.getText(R.string.settings).toString(), R.drawable.ic_settings_black_24dp  ))
        list.add(ListViewItemModel(resources.getText(R.string.about).toString(), R.drawable.ic_info_black_24dp  ))

        view.account_page_lv.adapter = ListViewAdapter(context!!, R.layout.view_item_listview, list)

        view.account_page_lv.onItemClickListener = onListViewItemClickListener
        view.profile_panel.setOnClickListener(onProfilePanelClickListener)

        return view
    }

    enum class AccountPageItems(val value : Int){
        ADS(0), SUBS(1), SETTINGS(2), ABOUT(3)
    }

    private val onListViewItemClickListener = AdapterView.OnItemClickListener{
        _, _, position, _ ->
        when(position){
//            AccountPageItems.ADS.value ->
//            AccountPageItems.SUBS.value ->
//            AccountPageItems.SETTINGS.value ->
//            AccountPageItems.ABOUT.value ->
        }
    }

    private val onProfilePanelClickListener = View.OnClickListener {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addSharedElement(avatar, "avatarTransition")
            ?.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            ?.addToBackStack(null)
            ?.replace(R.id.fragment_container, ProfileSettingsFragment())
            ?.commit()
    }
}
