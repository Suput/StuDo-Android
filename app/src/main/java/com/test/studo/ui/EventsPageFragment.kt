package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.test.studo.R
import com.test.studo.adapters.EventsRecyclerViewAdapter
import com.test.studo.api
import com.test.studo.api.models.CompactAd
import com.test.studo.compactAdList
import com.test.studo.currentUserWithToken
import kotlinx.android.synthetic.main.fragment_events_page.*
import kotlinx.android.synthetic.main.fragment_events_page.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_events_page, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.title_events)

        view.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        compactAdList?.let {
            view.rv.adapter = EventsRecyclerViewAdapter(compactAdList!!)
        } ?: run {
            getEvents(view)
        }

        view.swipe_container.setOnRefreshListener{
            getEvents(view)
            swipe_container.isRefreshing = false
        }

        return view
    }

    private fun getEvents(view : View){
        api.getAllAds("Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<List<CompactAd>>{
            override fun onResponse(call: Call<List<CompactAd>>, response: Response<List<CompactAd>>) {
                if (response.isSuccessful){
                    compactAdList = response.body()
                    view.rv.adapter = EventsRecyclerViewAdapter(compactAdList!!)
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }

            }

            override fun onFailure(call: Call<List<CompactAd>>, t: Throwable) {
                Toast.makeText(context, "No connection with server", Toast.LENGTH_LONG).show()
            }
        })
    }
}