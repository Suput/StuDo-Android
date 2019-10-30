package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.test.studo.R
import com.test.studo.adapters.ListViewAdapter
import com.test.studo.adapters.ListViewItemModel
import com.test.studo.api
import com.test.studo.api.models.User
import com.test.studo.currentUserWithToken
import com.test.studo.openFragment
import kotlinx.android.synthetic.main.fragment_account_page.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_account_page.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountPageFragment : Fragment() {

    lateinit var user : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_account_page, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.account)

        user = currentUserWithToken.user

        fillUserData(view)
        getCurrentUser(view)

        view.lv.adapter = ListViewAdapter(context!!, R.layout.listview_row, mutableListOf(
            ListViewItemModel(resources.getString(R.string.my_ads), R.drawable.ic_assignment_purple_24dp),
            ListViewItemModel(resources.getString(R.string.my_resumes), R.drawable.ic_assignment_ind_purple_24dp),
            ListViewItemModel(resources.getString(R.string.subs), R.drawable.ic_star_purple_24dp),
            ListViewItemModel(resources.getString(R.string.organizations), R.drawable.ic_group_purple_24dp),
            ListViewItemModel(resources.getString(R.string.settings), R.drawable.ic_settings_ic_purple_24dp),
            ListViewItemModel(resources.getString(R.string.about), R.drawable.ic_info_purple_24dp)
        ))

        view.lv.onItemClickListener = onListViewItemClickListener
        view.profile_panel.setOnClickListener(onProfilePanelClickListener)

        return view
    }

    private fun getCurrentUser(view : View){
        api.getUser(currentUserWithToken.user.id, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful){
                        user = response.body()!!
                        fillUserData(view)
                    }  else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()){
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun fillUserData(view : View){
        view.name_and_surname.text = resources.getString(
            R.string.name_and_surname,
            user.firstName,
            user.secondName
        )
        view.email.text = user.email
    }

    private enum class AccountPageItems{
        ADS, RESUMES, SUBS, ORGS, SETTINGS, ABOUT
    }

    private val onListViewItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        when (position) {
            AccountPageItems.ADS.ordinal -> {
                openUserFragment(AdsPageFragment())
            }
            AccountPageItems.RESUMES.ordinal -> {
                openUserFragment(ResumesPageFragment())
            }
            AccountPageItems.SUBS.ordinal -> {
                openBookmarksFragment()
            }
            AccountPageItems.ORGS.ordinal -> {
                openFragment(requireActivity(), OrganizationsPageFragment())
            }
            AccountPageItems.SETTINGS.ordinal -> {
                openFragment(requireActivity(), SettingsFragment())
            }
//            AccountPageItems.ABOUT.ordinal ->
        }
    }

    private val onProfilePanelClickListener = View.OnClickListener {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addSharedElement(avatar, avatar.transitionName)
            ?.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slide_to_left,
                R.anim.slide_from_left,
                R.anim.slide_to_right
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, ProfileSettingsFragment())
            ?.commit()
    }

    private fun openUserFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putSerializable("user", currentUserWithToken.user)
        fragment.arguments = bundle
        openFragment(requireActivity(), fragment)
    }

    private fun openBookmarksFragment(){
        val bundle = Bundle()
        bundle.putString("bookmarks", currentUserWithToken.accessToken)
        val fragment = AdsPageFragment()
        fragment.arguments = bundle
        openFragment(requireActivity(), fragment)
    }
}