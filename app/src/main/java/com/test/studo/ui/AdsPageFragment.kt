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
import com.test.studo.adapters.AdsRecyclerViewAdapter
import com.test.studo.api.models.CompactAd
import com.test.studo.api.models.Organization
import com.test.studo.api.models.User
import kotlinx.android.synthetic.main.fragment_ads_page.*
import kotlinx.android.synthetic.main.fragment_ads_page.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdsPageFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ads_page, container, false)

        view.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        arguments?.let {
            when {
                it.containsKey("organization") -> {
                    val organization = it.getSerializable("organization") as Organization

                    view.collapse_toolbar.title = organization.name

                    view.swipe_container.isRefreshing = true
                    getOrganizationAds(organization.id, this, view.swipe_container)

                    if (organization.creator == currentUserWithToken.user) {
                        view.create_ad_fab.show()
                        view.create_ad_fab.setOnClickListener{ openCreateAdFragment(organization) }
                    }

                    view.swipe_container.setOnRefreshListener {
                        getOrganizationAds(organization.id, this, view.swipe_container)
                    }
                }
                it.containsKey("user") -> {
                    val user = it.getSerializable("user") as User

                    view.collapse_toolbar.title = resources.getString(
                        R.string.name_and_surname,
                        user.firstName,
                        user.secondName
                    )
                    view.subtitle.text = resources.getString(R.string.ads)

                    view.swipe_container.isRefreshing = true
                    getUserAds(user.id, this, view.swipe_container)

                    if (user.id == currentUserWithToken.user.id) {
                        view.create_ad_fab.show()
                        view.create_ad_fab.setOnClickListener{ openCreateAdFragment() }
                    }

                    view.swipe_container.setOnRefreshListener {
                        getUserAds(user.id, this, view.swipe_container)
                    }
                }
                it.containsKey("bookmarks") -> {

                    view.collapse_toolbar.title = resources.getString(
                        R.string.name_and_surname,
                        currentUserWithToken.user.firstName,
                        currentUserWithToken.user.secondName
                    )
                    view.subtitle.text = resources.getString(R.string.bookmarks)

                    view.swipe_container.isRefreshing = true
                    getUserBookmarks(this, view.swipe_container)

                    view.swipe_container.setOnRefreshListener {
                        getUserBookmarks(this, view.swipe_container)
                    }
                }
            }
        } ?:run {
            view.collapse_toolbar.title = resources.getString(R.string.ads)

            allAdsList?.let {
                view.rv.adapter = AdsRecyclerViewAdapter(it, this)
            } ?: run {
                view.swipe_container.isRefreshing = true
                getAllAds(this, view.swipe_container)
            }

            view.swipe_container.setOnRefreshListener {
                getAllAds(this, view.swipe_container)
            }
        }

        return view
    }

    private fun getAllAds(adsPageFragment: AdsPageFragment, swipeRefreshLayout: SwipeRefreshLayout) {
        api.getAllAds("Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<List<CompactAd>> {
            override fun onResponse(call: Call<List<CompactAd>>, response: Response<List<CompactAd>>) {
                if (response.isSuccessful) {
                    allAdsList = response.body()
                    adsPageFragment.rv?.adapter = AdsRecyclerViewAdapter(allAdsList!!, adsPageFragment)
                    getUserBookmarks(adsPageFragment, swipeRefreshLayout, false)
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null && errorBodyText.isNotEmpty()) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<List<CompactAd>>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun getUserAds(userId: String, adsPageFragment: AdsPageFragment, swipeRefreshLayout: SwipeRefreshLayout) {
        api.getUserAds(userId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<List<CompactAd>> {
                override fun onResponse(call: Call<List<CompactAd>>, response: Response<List<CompactAd>>) {
                    if (response.isSuccessful) {
                        adsPageFragment.rv?.adapter = AdsRecyclerViewAdapter(response.body()!!, adsPageFragment)
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()) {
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

                override fun onFailure(call: Call<List<CompactAd>>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            })
    }

    private fun getOrganizationAds(organizationId: String, adsPageFragment: AdsPageFragment, swipeRefreshLayout: SwipeRefreshLayout) {
        api.getOrganizationAds(organizationId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<List<CompactAd>> {
                override fun onResponse(call: Call<List<CompactAd>>, response: Response<List<CompactAd>>) {
                    if (response.isSuccessful) {
                        adsPageFragment.rv?.adapter = AdsRecyclerViewAdapter(response.body()!!, adsPageFragment)
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()) {
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

                override fun onFailure(call: Call<List<CompactAd>>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            })
    }

    private fun getUserBookmarks(adsPageFragment: AdsPageFragment, swipeRefreshLayout: SwipeRefreshLayout, displayList : Boolean = true){
        api.getUserBookmarks("Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<List<CompactAd>> {
                override fun onResponse(call: Call<List<CompactAd>>, response: Response<List<CompactAd>>) {
                    if (response.isSuccessful) {
                        userBookmarksList = response.body()!!
                        if (displayList){
                            adsPageFragment.rv?.adapter = AdsRecyclerViewAdapter(response.body()!!, adsPageFragment)
                        }
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()) {
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

                override fun onFailure(call: Call<List<CompactAd>>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            })
    }

    fun onAdClick(compactAd: CompactAd) {

        val adFragment = AdFragment()
        val bundle = Bundle()
        bundle.putSerializable("compactAd", compactAd)
        adFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_from_top,
                R.anim.slide_to_bot,
                R.anim.slide_from_bot,
                R.anim.slide_to_top
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, adFragment)
            ?.commit()
    }

    private fun openCreateAdFragment(organization: Organization? = null) {
        val createAndEditAdFragment = CreateAndEditAdFragment()

        organization?.let {
            val bundle = Bundle()
            bundle.putSerializable("organization", it)
            createAndEditAdFragment.arguments = bundle
        }

        openFragment(requireActivity(), createAndEditAdFragment)
    }
}