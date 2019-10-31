package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.test.studo.*
import com.test.studo.adapters.CommentsRecyclerViewAdapter
import com.test.studo.api.models.Ad
import com.test.studo.api.models.CompactAd
import com.yydcdut.markdown.MarkdownProcessor
import com.yydcdut.markdown.syntax.edit.EditFactory
import com.yydcdut.markdown.syntax.text.TextFactory
import kotlinx.android.synthetic.main.bottom_sheet_comments.*
import kotlinx.android.synthetic.main.bottom_sheet_comments.view.*
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

        view.rv_comments.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        if (::ad.isInitialized) {
            fillAdData(view)
        }
        view.swipe_container.isRefreshing = true
        getAd(compactAd.id, view, view.swipe_container)

        view.show_comments_btn.setOnClickListener {
            when(it.tag){
                "Hidden" -> {
                    show_comments_btn.text = getString(R.string.hide_comments)
                    comments_bottom_sheet.visibility = View.VISIBLE
                    it.tag = "Showed"
                }
                "Showed" -> {
                    show_comments_btn.text = getString(R.string.show_comments)
                    comments_bottom_sheet.visibility = View.GONE
                    it.tag = "Hidden"
                }
            }
        }

        view.swipe_container.setOnRefreshListener { getAd(compactAd.id, view, view.swipe_container) }

        view.creator_panel.setOnClickListener{ openCreatorProfileFragment() }

        view.subscribe_btn.setOnClickListener {
            when(it.tag){
                "Sub" -> {
                    removeFromBookmarks(ad.id)
                    it.setBackgroundResource(R.drawable.ic_star_border_purple_24dp)
                    it.tag = "UnSub"
                }
                "UnSub" -> {
                    addToBookmarks(ad.id)
                    it.setBackgroundResource(R.drawable.ic_star_purple_24dp)
                    it.tag = "Sub"
                }
            }
        }

        return view
    }

    private fun getAd(adId : String, view: View, swipeRefreshLayout: SwipeRefreshLayout){
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
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<Ad>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun addToBookmarks(adId : String){
        api.addToBookmarks(adId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        Toast.makeText(context, getString(R.string.added_bookmarks), Toast.LENGTH_LONG).show()
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()){
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun removeFromBookmarks(adId : String){
        api.removeFromBookmarks(adId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        Toast.makeText(context, getString(R.string.removed_bookmarks), Toast.LENGTH_LONG).show()
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()){
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun fillAdData(view : View){

        view.name.text = ad.name
        view.short_description.text = ad.shortDescription

        // Markdown desc
        val markdownProcessor = MarkdownProcessor(requireContext())
        markdownProcessor.factory(TextFactory.create())
        view.description.text = markdownProcessor.parse(ad.description)

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

        ad.comments?.let {
            view.rv_comments.adapter = CommentsRecyclerViewAdapter(requireContext(), it)

            val dividerItemDecoration = DividerItemDecoration(view.rv_comments.context, RecyclerView.VERTICAL)
            view.rv_comments.addItemDecoration(dividerItemDecoration)
        }

        if (ad.user?.id == currentUserWithToken.user.id){
            view.edit_ad_fab.show()
            view.edit_ad_fab.setOnClickListener{ openEditAdFragment() }
        }

        userBookmarksList?.let{
            for(compactAd in it){
                if (compactAd.id == ad.id){
                    with(view.subscribe_btn){
                        setBackgroundResource(R.drawable.ic_star_purple_24dp)
                        tag = "Sub"
                    }
                }
            }
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

        openFragment(requireActivity(), createAndEditAdFragment)
    }
}
