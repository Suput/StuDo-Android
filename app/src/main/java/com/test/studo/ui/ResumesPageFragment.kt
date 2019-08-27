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
import com.test.studo.adapters.ResumesRecyclerViewAdapter
import com.test.studo.api
import com.test.studo.api.models.CompactResume
import com.test.studo.api.models.User
import com.test.studo.compactResumeList
import com.test.studo.currentUserWithToken
import kotlinx.android.synthetic.main.fragment_resumes_page.*
import kotlinx.android.synthetic.main.fragment_resumes_page.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ResumesPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_resumes_page, container, false)

        view.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val bundle = arguments
        val user = bundle?.getSerializable("user") as User?
        if (bundle != null && user != null){
            getUserResumes(user.id, this)

            view.collapse_toolbar.title = user.firstName + " " + user.secondName
            view.subtitle.text = resources.getString(R.string.title_resumes)

            if (user == currentUserWithToken.user){
                view.fab.show()
                view.fab.setOnClickListener(onFabClickListener)
            }

            view.swipe_container.setOnRefreshListener{ getUserResumes(user.id, this, swipe_container) }
        } else {
            view.collapse_toolbar.title = resources.getString(R.string.title_resumes)

            compactResumeList?.let {
                view.rv.adapter = ResumesRecyclerViewAdapter(it, this)
            } ?: run {
                getAllResumes(this)
            }

            view.swipe_container.setOnRefreshListener{ getAllResumes(this, swipe_container) }
        }

        return view
    }

    private fun getAllResumes(resumesPageFragment: ResumesPageFragment, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getAllResumes("Bearer " + currentUserWithToken.accessToken).enqueue(object :
            Callback<List<CompactResume>> {
            override fun onResponse(call: Call<List<CompactResume>>, response: Response<List<CompactResume>>) {
                if (response.isSuccessful){
                    compactResumeList = response.body()
                    resumesPageFragment.rv?.adapter = ResumesRecyclerViewAdapter(compactResumeList!!, resumesPageFragment)
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
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    private fun getUserResumes(userId : String, resumesPageFragment: ResumesPageFragment, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getUserResumes(userId, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<List<CompactResume>> {
            override fun onResponse(call: Call<List<CompactResume>>, response: Response<List<CompactResume>>) {
                if (response.isSuccessful){
                    resumesPageFragment.rv?.adapter = ResumesRecyclerViewAdapter(response.body()!!, resumesPageFragment)
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
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    fun onResumeClick(resumePanel : LinearLayout, compactResume: CompactResume){

        resumePanel.transitionName = "resume_panel_transition"

        val resumeFragment = ResumeFragment()
        val bundle = Bundle()
        bundle.putString("resumeId", compactResume.id)
        bundle.putString("name", compactResume.name)
        resumeFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addSharedElement(resumePanel, resumePanel.transitionName)
            ?.setCustomAnimations(R.anim.slide_from_top, R.anim.slide_to_bot, R.anim.slide_from_bot, R.anim.slide_to_top)
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, resumeFragment)
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
            ?.replace(R.id.main_fragment_container, CreateAndEditResumeFragment())
            ?.commit()
    }
}
