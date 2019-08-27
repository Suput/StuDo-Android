package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.test.studo.R
import com.test.studo.adapters.AdsRecyclerViewAdapter
import com.test.studo.api
import com.test.studo.api.models.CompactAd
import com.test.studo.api.models.User
import com.test.studo.compactAdList
import com.test.studo.currentUserWithToken
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

        val bundle = arguments
        val user = bundle?.getSerializable("user") as User?
        if (bundle != null && user != null) {
            getUserAds(user.id, this)

            view.collapse_toolbar.title = user.firstName + " " + user.secondName
            view.subtitle.text = resources.getString(R.string.title_ads)

            if (user == currentUserWithToken.user) {
                view.fab.show()
                view.fab.setOnClickListener(onFabClickListener)
            }

            view.swipe_container.setOnRefreshListener { getUserAds(user.id, this, swipe_container) }
        } else {
            view.collapse_toolbar.title = resources.getString(R.string.title_ads)

            compactAdList?.let {
                view.rv.adapter = AdsRecyclerViewAdapter(it, this)
            } ?: run {
                getAllAds(this)
            }

            view.swipe_container.setOnRefreshListener { getAllAds(this, swipe_container) }
        }

        return view
    }

    private fun getAllAds(adsPageFragment: AdsPageFragment, swipeRefreshLayout: SwipeRefreshLayout? = null) {
        api.getAllAds("Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<List<CompactAd>> {
            override fun onResponse(call: Call<List<CompactAd>>, response: Response<List<CompactAd>>) {
                if (response.isSuccessful) {
                    compactAdList = response.body()
                    adsPageFragment.rv?.adapter = AdsRecyclerViewAdapter(compactAdList!!, adsPageFragment)
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
                swipeRefreshLayout?.isRefreshing = false
            }

            override fun onFailure(call: Call<List<CompactAd>>, t: Throwable) {
                Toast.makeText(context, "resources.getText(R.string.connection_with_server_error)", Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    private fun getUserAds(
        userId: String,
        adsPageFragment: AdsPageFragment,
        swipeRefreshLayout: SwipeRefreshLayout? = null
    ) {
        api.getUserAds(userId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<List<CompactAd>> {
                override fun onResponse(call: Call<List<CompactAd>>, response: Response<List<CompactAd>>) {
                    if (response.isSuccessful) {
                        adsPageFragment.rv?.adapter = AdsRecyclerViewAdapter(response.body()!!, adsPageFragment)
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
                }

                override fun onFailure(call: Call<List<CompactAd>>, t: Throwable) {
                    Toast.makeText(context, "resources.getText(R.string.connection_with_server_error)", Toast.LENGTH_LONG).show()
                    swipeRefreshLayout?.isRefreshing = false
                }
            })
    }

    fun onAdClick(adPanel: LinearLayout, compactAd: CompactAd) {

        adPanel.transitionName = "ad_panel_transition"

        val adFragment = AdFragment()
        val bundle = Bundle()
        bundle.putString("adId", compactAd.id)
        bundle.putString("name", compactAd.name)
        bundle.putString("short_description", compactAd.shortDescription)
        adFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addSharedElement(adPanel, adPanel.transitionName)
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
            ?.replace(R.id.main_fragment_container, CreateAndEditAdFragment())
            ?.commit()
    }
}