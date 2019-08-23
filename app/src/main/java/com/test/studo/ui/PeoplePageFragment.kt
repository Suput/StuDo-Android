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
import com.test.studo.adapters.PeopleRecyclerViewAdapter
import com.test.studo.api
import com.test.studo.api.models.CompactResume
import com.test.studo.compactResumeList
import com.test.studo.currentUserWithToken
import kotlinx.android.synthetic.main.fragment_people_page.*
import kotlinx.android.synthetic.main.fragment_people_page.view.rv
import kotlinx.android.synthetic.main.fragment_people_page.view.swipe_container
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PeoplePageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_people_page, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.title_people)

        view.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        compactResumeList?.let {
            view.rv.adapter = PeopleRecyclerViewAdapter(compactResumeList!!)
        } ?: run {
            getResumes(view)
        }

        view.swipe_container.setOnRefreshListener{
            getResumes(view)
            swipe_container.isRefreshing = false
        }

        return view
    }

    private fun getResumes(view : View){
        api.getAllResumes("Bearer " + currentUserWithToken.accessToken).enqueue(object :
            Callback<List<CompactResume>> {
            override fun onResponse(call: Call<List<CompactResume>>, response: Response<List<CompactResume>>) {
                if (response.isSuccessful){
                    compactResumeList = response.body()
                    view.rv.adapter = PeopleRecyclerViewAdapter(compactResumeList!!)
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }

            }

            override fun onFailure(call: Call<List<CompactResume>>, t: Throwable) {
                Toast.makeText(context, "No connection with server", Toast.LENGTH_LONG).show()
            }
        })
    }
}
