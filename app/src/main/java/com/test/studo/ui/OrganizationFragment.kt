package com.test.studo.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.test.studo.R
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
        view.name.text = bundle?.getString("name")

        if (::organization.isInitialized){
            view.name.text = organization.name
            view.description.text = organization.description
            view.creator_name_and_surname.text = organization.creator.firstName + " " + organization.creator.secondName

            if (organization.creator.id == currentUserWithToken.user.id){
                view.fab.show()
            }
        } else {
            arguments?.getString("organizationId")?.let { getOrganization(it) }
        }

        view.separator_1.visibility = View.VISIBLE
        view.separator_2.visibility = View.VISIBLE

        view.swipe_container.setOnRefreshListener { arguments?.getString("organizationId")?.let {getOrganization(it, view.swipe_container)} }

        view.creator_panel.setOnClickListener(onProfilePanelClickListener)

        view.fab.setOnClickListener(onFabClickListener)
        
        return view
    }

    private val onProfilePanelClickListener = View.OnClickListener {

        if (::organization.isInitialized){
            val userProfileFragment = UserProfileFragment()
            val bundle = Bundle()

            bundle.putSerializable("user", organization.creator)
            userProfileFragment.arguments = bundle

            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.addSharedElement(avatar, avatar.transitionName)
                ?.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
                ?.addToBackStack(null)
                ?.replace(R.id.main_fragment_container, userProfileFragment)
                ?.commit()
        }
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
                    creator_name_and_surname?.text = organization.creator.firstName + " " + organization.creator.secondName

                    if (organization.creator.id == currentUserWithToken.user.id){
                        fab?.show()
                    }
                }  else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
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
