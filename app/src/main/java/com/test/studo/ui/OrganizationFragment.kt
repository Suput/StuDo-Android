package com.test.studo.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast

import com.test.studo.R
import com.test.studo.adapters.ListViewAdapter
import com.test.studo.adapters.ListViewItemModel
import com.test.studo.api
import com.test.studo.api.models.Organization
import com.test.studo.currentUserWithToken
import com.test.studo.openFragment
import kotlinx.android.synthetic.main.fragment_organization.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrganizationFragment : Fragment() {

    lateinit var organization : Organization
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_organization, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.organization)

        organization = arguments!!.getSerializable("organization") as Organization

        fillOrganizationData(view)
        view.swipe_container.isRefreshing = true
        getOrganization(organization.id, view, view.swipe_container)

        view.swipe_container.setOnRefreshListener { getOrganization(organization.id, view, view.swipe_container) }

        view.lv.adapter = ListViewAdapter(context!!, R.layout.listview_row, mutableListOf(
            ListViewItemModel(resources.getString(R.string.ads).toString(), R.drawable.ic_assignment_purple_24dp),
            ListViewItemModel(resources.getString(R.string.members).toString(), R.drawable.ic_group_purple_24dp)
        ))
        view.lv.onItemClickListener = onListViewItemClickListener

        return view
    }

    private fun getOrganization(organizationId : String, view : View, swipeRefreshLayout: SwipeRefreshLayout){
        api.getOneOrganization(organizationId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                if (response.isSuccessful){
                    organization = response.body()!!
                    fillOrganizationData(view)
                }  else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null && errorBodyText.isNotEmpty()){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun fillOrganizationData(view : View){
        view.name.text = organization.name
        view.description.text = organization.description

        if (organization.creator.id == currentUserWithToken.user.id){
            view.edit_organization_fab.show()
            view.edit_organization_fab.setOnClickListener{ openEditOrganizationFragment() }
        }
    }

    private enum class OrganizationItems{
        ADS, MEMBERS
    }

    private val onListViewItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        when (position) {
            OrganizationItems.ADS.ordinal -> {
                openOrganizationAdsFragment()
            }
//            OrganizationItems.MEMBERS.value -> {
//                openFragment(ResumesPageFragment())
//            }
        }
    }

    private fun openOrganizationAdsFragment(){
        val adsPageFragment = AdsPageFragment()
        val bundle = Bundle()
        bundle.putSerializable("organization", organization)
        adsPageFragment.arguments = bundle

        openFragment(requireActivity(), adsPageFragment)
    }

    private fun openEditOrganizationFragment(){
        val createAndEditOrganizationFragment = CreateAndEditOrganizationFragment()
        val bundle = Bundle()
        bundle.putSerializable("organization", organization)
        createAndEditOrganizationFragment.arguments = bundle

        openFragment(requireActivity(), createAndEditOrganizationFragment)
    }
}
