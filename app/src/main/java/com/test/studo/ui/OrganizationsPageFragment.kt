package com.test.studo.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.test.studo.R
import com.test.studo.adapters.OrganizationsRecyclerViewAdapter
import com.test.studo.api
import com.test.studo.api.models.Organization
import com.test.studo.currentUserWithToken
import com.test.studo.organizationList
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

        view.collapse_toolbar.title = resources.getText(R.string.organizations)

        view.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        organizationList?.let {
            view.rv.adapter = OrganizationsRecyclerViewAdapter(it, this)
        } ?: run {
            getAllOrganizations(this)
        }

        view.swipe_container.setOnRefreshListener { getAllOrganizations(this, view.swipe_container) }

        view.fab.setOnClickListener(onFabClickListener)

        return view
    }

    private fun getAllOrganizations(organizationsPageFragment: OrganizationsPageFragment, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getAllOrganizations("Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<List<Organization>>{
            override fun onResponse(call: Call<List<Organization>>, response: Response<List<Organization>>) {
                if (response.isSuccessful) {
                    organizationList = response.body()
                    organizationsPageFragment.rv?.adapter = OrganizationsRecyclerViewAdapter(organizationList!!, organizationsPageFragment)
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
                swipeRefreshLayout?.isRefreshing = false

                Log.wtf("body", response.raw().toString())
            }

            override fun onFailure(call: Call<List<Organization>>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    private val onFabClickListener = View.OnClickListener {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slide_to_left,
                R.anim.slide_from_left,
                R.anim.slide_to_right
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, CreateAndEditOrganizationFragment())
            ?.commit()
    }
}
