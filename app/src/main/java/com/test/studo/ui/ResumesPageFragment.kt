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
import com.test.studo.adapters.ResumesRecyclerViewAdapter
import com.test.studo.api.models.CompactResume
import com.test.studo.api.models.User
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

        arguments?.let {
            if (it.containsKey("user")) {
                val user = it.getSerializable("user") as User

                view.collapse_toolbar.title = resources.getString(
                    R.string.name_and_surname,
                    user.firstName,
                    user.secondName
                )
                view.subtitle.text = resources.getString(R.string.resumes)

                view.swipe_container.isRefreshing = true
                getUserResumes(user.id, this, view.swipe_container)

                if (user.id == currentUserWithToken.user.id) {
                    view.create_resume_fab.show()
                    view.create_resume_fab.setOnClickListener{
                        openFragment(requireActivity(), CreateAndEditResumeFragment())
                    }
                }

                view.swipe_container.setOnRefreshListener {
                    getUserResumes(user.id, this, swipe_container)
                }
            }
        } ?:run {
            view.collapse_toolbar.title = resources.getString(R.string.resumes)

            allResumesList?.let {
                view.rv.adapter = ResumesRecyclerViewAdapter(it, this)
            } ?: run {
                view.swipe_container.isRefreshing = true
                getAllResumes(this, view.swipe_container)
            }

            view.swipe_container.setOnRefreshListener {
                getAllResumes(this, view.swipe_container)
            }
        }

        return view
    }

    private fun getAllResumes(resumesPageFragment: ResumesPageFragment, swipeRefreshLayout: SwipeRefreshLayout){
        api.getAllResumes("Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<List<CompactResume>> {
            override fun onResponse(call: Call<List<CompactResume>>, response: Response<List<CompactResume>>) {
                if (response.isSuccessful){
                    allResumesList = response.body()
                    resumesPageFragment.rv?.adapter = ResumesRecyclerViewAdapter(allResumesList!!, resumesPageFragment)
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

            override fun onFailure(call: Call<List<CompactResume>>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun getUserResumes(userId : String, resumesPageFragment: ResumesPageFragment, swipeRefreshLayout: SwipeRefreshLayout){
        api.getUserResumes(userId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<List<CompactResume>> {
            override fun onResponse(call: Call<List<CompactResume>>, response: Response<List<CompactResume>>) {
                if (response.isSuccessful){
                    resumesPageFragment.rv?.adapter = ResumesRecyclerViewAdapter(response.body()!!, resumesPageFragment)
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

            override fun onFailure(call: Call<List<CompactResume>>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    fun onResumeClick(compactResume: CompactResume){

        val resumeFragment = ResumeFragment()
        val bundle = Bundle()
        bundle.putSerializable("compactResume", compactResume)
        resumeFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_from_top,
                R.anim.slide_to_bot,
                R.anim.slide_from_bot,
                R.anim.slide_to_top
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, resumeFragment)
            ?.commit()
    }
}
