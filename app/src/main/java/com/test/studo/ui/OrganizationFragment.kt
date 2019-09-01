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
import kotlinx.android.synthetic.main.fragment_organization.*
import kotlinx.android.synthetic.main.fragment_organization.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrganizationFragment : Fragment() {

    lateinit var organization : Organization
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_organization, container, false)

        view.collapse_toolbar.title = resources.getText(R.string.organization)

        val bundle = this.arguments
        organization = bundle?.getSerializable("organization") as Organization

        view.name.text = organization.name
        view.description.text = organization.description

        if (organization.creator.id == currentUserWithToken.user.id){
            view.edit_organization_fab.show()
        }

        view.swipe_container.setOnRefreshListener { getOrganization(organization.id, view.swipe_container) }

        view.edit_organization_fab.setOnClickListener(onFabClickListener)

        val list = mutableListOf(
            ListViewItemModel(resources.getText(R.string.ads).toString(), R.drawable.ic_assignment_blue_24dp),
            ListViewItemModel(resources.getText(R.string.members).toString(), R.drawable.ic_group_blue_24dp)
        )

        view.lv.adapter = ListViewAdapter(context!!, R.layout.view_item_listview, list)
        view.lv.onItemClickListener = onListViewItemClickListener

        return view
    }

    enum class OrganizationItems(val value: Int) {
        ADS(0), MEMBERS(1)
    }

    private val onListViewItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        when (position) {
            OrganizationItems.ADS.value -> {
                openFragment(AdsPageFragment())
            }
//            OrganizationItems.MEMBERS.value -> {
//                openFragment(ResumesPageFragment())
//            }
        }
    }

    private fun openFragment(fragment: Fragment){
        val bundle = Bundle()
        bundle.putSerializable("organization", organization)
        fragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slide_to_left,
                R.anim.slide_from_left,
                R.anim.slide_to_right
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, fragment)
            ?.commit()
    }

    private val onFabClickListener = View.OnClickListener {
        val createAndEditOrganizationFragment = CreateAndEditOrganizationFragment()
        val bundle = Bundle()
        bundle.putSerializable("organization", organization)
        createAndEditOrganizationFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slide_to_left,
                R.anim.slide_from_left,
                R.anim.slide_to_right
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, createAndEditOrganizationFragment)
            ?.commit()
    }

    private fun getOrganization(organizationId : String, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getOneOrganization(organizationId, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                if (response.isSuccessful){
                    organization = response.body()!!

                    name?.text = organization.name
                    description?.text = organization.description

                    if (organization.creator.id == currentUserWithToken.user.id){
                        edit_organization_fab?.show()
                    }
                }  else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != ""){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }

                swipeRefreshLayout?.isRefreshing = false
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }
}
