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

        view.collapse_toolbar.title = resources.getString(R.string.title_resumes)

        view.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val bundle = arguments
        val userId = bundle?.getString("userId")
        if (bundle != null && userId != null){
            getUserResumes(userId, this)

            view.swipe_container.setOnRefreshListener{ getUserResumes(userId, this, swipe_container) }
        } else {
            compactResumeList?.let {
                view.rv.adapter = PeopleRecyclerViewAdapter(compactResumeList!!, this)
            } ?: run {
                getAllResumes(this)
            }

            view.swipe_container.setOnRefreshListener{ getAllResumes(this, swipe_container) }
        }

        return view
    }

    private fun getAllResumes(peoplePageFragment: PeoplePageFragment, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getAllResumes("Bearer " + currentUserWithToken.accessToken).enqueue(object :
            Callback<List<CompactResume>> {
            override fun onResponse(call: Call<List<CompactResume>>, response: Response<List<CompactResume>>) {
                if (response.isSuccessful){
                    compactResumeList = response.body()
                    peoplePageFragment.rv?.adapter = PeopleRecyclerViewAdapter(compactResumeList!!, peoplePageFragment)
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

            override fun onFailure(call: Call<List<CompactResume>>, t: Throwable) {
                Toast.makeText(context, "No connection with server", Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    private fun getUserResumes(userId : String, peoplePageFragment: PeoplePageFragment, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getUserResumes(userId, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<List<CompactResume>> {
            override fun onResponse(call: Call<List<CompactResume>>, response: Response<List<CompactResume>>) {
                if (response.isSuccessful){
                    peoplePageFragment.rv?.adapter = PeopleRecyclerViewAdapter(response.body()!!, peoplePageFragment)
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

            override fun onFailure(call: Call<List<CompactResume>>, t: Throwable) {
                Toast.makeText(context, "No connection with server", Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    fun onResumeClick(resumePanel : LinearLayout, compactResume: CompactResume){

        resumePanel.transitionName = "resume_panel_transition"

        val peopleFragment = ResumeFragment()
        val bundle = Bundle()
        bundle.putString("resumeId", compactResume.id)
        bundle.putString("name", compactResume.name)
        peopleFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addSharedElement(resumePanel, resumePanel.transitionName)
            ?.setCustomAnimations(R.anim.slide_from_top, R.anim.slide_to_bot, R.anim.slide_from_bot, R.anim.slide_to_top)
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, peopleFragment)
            ?.commit()
    }
}
