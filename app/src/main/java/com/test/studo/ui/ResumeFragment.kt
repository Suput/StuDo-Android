package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.test.studo.R
import com.test.studo.api
import com.test.studo.api.models.CompactResume
import com.test.studo.api.models.Resume
import com.test.studo.currentUserWithToken
import com.test.studo.openFragment
import kotlinx.android.synthetic.main.fragment_resume.*
import kotlinx.android.synthetic.main.fragment_resume.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResumeFragment : Fragment() {

    lateinit var resume : Resume

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_resume, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.resume)

        val compactResume = arguments!!.getSerializable("compactResume") as CompactResume

        view.name.text = compactResume.name

        if (::resume.isInitialized){
            fillResumeData(view)
        }
        view.swipe_container.isRefreshing = true
        getResume(compactResume.id, view, view.swipe_container)

        view.swipe_container.setOnRefreshListener { getResume(compactResume.id, view, view.swipe_container)}

        view.creator_panel.setOnClickListener{ openCreatorProfileFragment() }

        return view
    }

    private fun getResume(resumeId : String, view : View, swipeRefreshLayout: SwipeRefreshLayout){
        api.getOneResume(resumeId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Resume> {
                override fun onResponse(call: Call<Resume>, response: Response<Resume>) {
                    if (response.isSuccessful){
                        resume = response.body()!!
                        fillResumeData(view)
                    }  else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()){
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                    swipeRefreshLayout.isRefreshing = false
                }

                override fun onFailure(call: Call<Resume>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            })
    }

    private fun fillResumeData(view : View){
        view.name.text = resume.name
        view.description.text = resume.description
        view.creator_name_and_surname.text = resources.getString(
            R.string.name_and_surname,
            resume.user.firstName,
            resume.user.secondName
        )

        if (resume.user.id == currentUserWithToken.user.id){
            view.edit_resume_fab.show()
            view.edit_resume_fab.setOnClickListener{ openEditResumeFragment() }
        }
    }

    private fun openCreatorProfileFragment(){
        if (::resume.isInitialized){
            val userProfileFragment = UserProfileFragment()
            val bundle = Bundle()
            bundle.putSerializable("user", resume.user)
            userProfileFragment.arguments = bundle

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
                ?.replace(R.id.main_fragment_container, userProfileFragment)
                ?.commit()
        }
    }

    private fun openEditResumeFragment(){
        val createAndEditResumeFragment = CreateAndEditResumeFragment()
        val bundle = Bundle()
        bundle.putSerializable("resume", resume)
        createAndEditResumeFragment.arguments = bundle

        openFragment(requireActivity(), createAndEditResumeFragment)
    }
}
