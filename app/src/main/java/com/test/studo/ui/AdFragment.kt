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

        view.collapse_toolbar.title = resources.getText(R.string.ad)

        view.name.text = arguments?.getString("name")
        view.short_description.text = arguments?.getString("short_description")

        if (::ad.isInitialized){
            view.name.text = ad.name
            view.short_description.text = ad.shortDescription
            view.description.text = ad.description
            ad.organizationId?.let{
                view.creator.text = ad.organization?.name
            } ?: run{
                view.creator.text = ad.user?.firstName + " " + ad.user?.secondName
            }

            view.begin_time.text = clientDataFormat.format(serverDataFormat.parse(ad.beginTime))
            view.end_time.text = clientDataFormat.format(serverDataFormat.parse(ad.endTime))

            if (ad.user?.id == currentUserWithToken.user.id){
                view.fab.show()
            }
        } else {
            arguments?.getString("adId")?.let { getAd(it) }
        }

        view.separator_1.visibility = View.VISIBLE
        view.separator_2.visibility = View.VISIBLE

        view.swipe_container.setOnRefreshListener { arguments?.getString("adId")?.let {getAd(it, view.swipe_container)} }

        view.creator_panel.setOnClickListener(onProfilePanelClickListener)

        view.fab.setOnClickListener(onFabClickListener)

        return view
    }

    private val onProfilePanelClickListener = View.OnClickListener {

        if (::ad.isInitialized){
            val bundle = Bundle()

            lateinit var fragment : Fragment

            ad.organizationId?.let{
                fragment = OrganizationFragment()
                bundle.putSerializable("organization", ad.organization)
            } ?: run{
                fragment= UserProfileFragment()
                bundle.putSerializable("user", ad.user)
            }
            fragment.arguments = bundle

            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.addSharedElement(avatar, avatar.transitionName)
                ?.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
                ?.addToBackStack(null)
                ?.replace(R.id.main_fragment_container, fragment)
                ?.commit()
        }
    }

    private val onFabClickListener = View.OnClickListener {
        val createAndEditAdFragment = CreateAndEditAdFragment()
        val bundle = Bundle()
        bundle.putSerializable("ad", ad)
        createAndEditAdFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slide_to_left,
                R.anim.slide_from_left,
                R.anim.slide_to_right
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, createAndEditAdFragment)
            ?.commit()
    }

    private fun getAd(adId : String, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getOneAd(adId, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Ad>{
            override fun onResponse(call: Call<Ad>, response: Response<Ad>) {
                if (response.isSuccessful){
                    ad = response.body()!!

                    name?.text = ad.name
                    short_description?.text = ad.shortDescription
                    description?.text = ad.description
                    ad.organizationId?.let{
                        creator?.text = ad.organization?.name
                    } ?: run{
                        creator?.text = ad.user?.firstName + " " + ad.user?.secondName
                    }

                    try{
                        begin_time?.text = clientDataFormat.format(serverDataFormat.parse(ad.beginTime))
                    } catch(e : Exception){
                        begin_time?.text = clientDataFormat.format(serverDataFormatWithoutMillis.parse(ad.beginTime))
                    }

                    try{
                        end_time?.text = clientDataFormat.format(serverDataFormat.parse(ad.endTime))
                    } catch(e : Exception){
                        end_time?.text = clientDataFormat.format(serverDataFormatWithoutMillis.parse(ad.endTime))
                    }

                    if (ad.user?.id == currentUserWithToken.user.id){
                        fab?.show()
                    }
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }

                swipeRefreshLayout?.isRefreshing = false
            }

            override fun onFailure(call: Call<Ad>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }
}
