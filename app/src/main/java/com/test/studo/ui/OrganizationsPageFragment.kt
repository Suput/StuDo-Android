package com.test.studo.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.test.studo.*

import com.test.studo.adapters.OrganizationsRecyclerViewAdapter
import com.test.studo.api.models.Organization
import kotlinx.android.synthetic.main.fragment_organizations_page.*
import kotlinx.android.synthetic.main.fragment_organizations_page.view.*
import kotlinx.android.synthetic.main.fragment_organizations_page.view.rv
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrganizationsPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_organizations_page, container, false)

        view.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        view.collapse_toolbar.title = resources.getString(R.string.organizations)

        allOrganizationsList?.let {
            view.rv.adapter = OrganizationsRecyclerViewAdapter(it, this)
        } ?: run {
            getAllOrganizations(this)
        }

        view.swipe_container.setOnRefreshListener {
            getAllOrganizations(this, view.swipe_container)
        }

        view.create_organization_fab.setOnClickListener{ openFragment(activity, CreateAndEditOrganizationFragment()) }

        return view
    }

    private fun getAllOrganizations(organizationsPageFragment: OrganizationsPageFragment, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getAllOrganizations("Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<List<Organization>>{
            override fun onResponse(call: Call<List<Organization>>, response: Response<List<Organization>>) {
                if (response.isSuccessful) {
                    allOrganizationsList = response.body()
                    organizationsPageFragment.rv?.adapter = OrganizationsRecyclerViewAdapter(allOrganizationsList!!, organizationsPageFragment)
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null && errorBodyText.isNotEmpty()) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
                swipeRefreshLayout?.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Organization>>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    fun onOrganizationClick(organization: Organization){
        val organizationFragment = OrganizationFragment()
        val bundle = Bundle()
        bundle.putSerializable("organization", organization)
        organizationFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_from_top,
                R.anim.slide_to_bot,
                R.anim.slide_from_bot,
                R.anim.slide_to_top
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, organizationFragment)
            ?.commit()
    }
}
