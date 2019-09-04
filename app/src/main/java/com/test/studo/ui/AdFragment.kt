package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.test.studo.*
import com.test.studo.api.models.Ad
import com.test.studo.api.models.CompactAd
import kotlinx.android.synthetic.main.fragment_ad.*
import kotlinx.android.synthetic.main.fragment_ad.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AdFragment : Fragment() {

    lateinit var ad : Ad

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ad, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.ad)

        val compactAd = arguments!!.getSerializable("compactAd") as CompactAd

        view.name.text = compactAd.name
        view.short_description.text = compactAd.shortDescription

        if (::ad.isInitialized) {
            fillAdData(view)
        }
        getAd(compactAd.id, view)

        view.swipe_container.setOnRefreshListener { getAd(compactAd.id, view, view.swipe_container) }

        view.creator_panel.setOnClickListener{ openCreatorProfileFragment() }

        return view
    }

    private fun getAd(adId : String, view: View, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getOneAd(adId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Ad>{
            override fun onResponse(call: Call<Ad>, response: Response<Ad>) {
                if (response.isSuccessful){
                    ad = response.body()!!
                    fillAdData(view)
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null && errorBodyText.isNotEmpty()){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
                swipeRefreshLayout?.isRefreshing = false
            }

            override fun onFailure(call: Call<Ad>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    private fun fillAdData(view : View){

        view.name.text = ad.name
        view.short_description.text = ad.shortDescription
        view.description.text = ad.description

        ad.organization?.let{
            view.creator_name.text = it.name
        } ?: run{
            view.creator_name.text = resources.getString(
                R.string.name_and_surname,
                ad.user?.firstName,
                ad.user?.secondName)
        }

        try{
            view.begin_time?.text = clientDataFormat.format(serverDataFormat.parse(ad.beginTime))
        } catch(e : Exception){
            view.begin_time?.text = clientDataFormat.format(serverDataFormatWithoutMillis.parse(ad.beginTime))
        }

        try{
            view.end_time?.text = clientDataFormat.format(serverDataFormat.parse(ad.endTime))
        } catch(e : Exception){
            view.end_time?.text = clientDataFormat.format(serverDataFormatWithoutMillis.parse(ad.endTime))
        }

        if (ad.user?.id == currentUserWithToken.user.id){
            view.edit_ad_fab.show()
            view.edit_ad_fab.setOnClickListener{ openEditAdFragment() }
        }
    }

    private fun openCreatorProfileFragment(){
        if (::ad.isInitialized){
            val bundle = Bundle()
            lateinit var fragment : Fragment

            ad.organization?.let{
                fragment = OrganizationFragment()
                bundle.putSerializable("organization", it)
            } ?: run{
                fragment= UserProfileFragment()
                bundle.putSerializable("user", ad.user)
            }

            fragment.arguments = bundle

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
                ?.replace(R.id.main_fragment_container, fragment)
                ?.commit()
        }
    }

    private fun openEditAdFragment(){
        val createAndEditAdFragment = CreateAndEditAdFragment()
        val bundle = Bundle()
        bundle.putSerializable("ad", ad)
        createAndEditAdFragment.arguments = bundle

        openFragment(activity, createAndEditAdFragment)
    }
}
