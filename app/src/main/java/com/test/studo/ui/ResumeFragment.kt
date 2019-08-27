package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.test.studo.R
import com.test.studo.api
import com.test.studo.api.models.Resume
import com.test.studo.currentUserWithToken
import kotlinx.android.synthetic.main.fragment_resume.*
import kotlinx.android.synthetic.main.fragment_resume.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResumeFragment : Fragment() {

    lateinit var resume : Resume

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_resume, container, false)

        view.collapse_toolbar.title = resources.getText(R.string.resume)

        val bundle = this.arguments
        view.name.text = bundle?.getString("name")
        view.separator_1.visibility = View.VISIBLE
        view.separator_2.visibility = View.VISIBLE

        if (::resume.isInitialized){
            view.name.text = resume.name
            view.description.text = resume.description
            view.creator_name_and_surname.text = resume.user.firstName + " " + resume.user.secondName

            if (resume.user.id == currentUserWithToken.user.id){
                view.fab.show()
            }
        } else {
            arguments?.getString("resumeId")?.let { getResume(it) }
        }

        view.swipe_container.setOnRefreshListener { arguments?.getString("resumeId")?.let {getResume(it, swipe_container)} }

        view.creator_panel.setOnClickListener(onProfilePanelClickListener)

        view.fab.setOnClickListener(onFabClickListener)

        return view
    }

    private val onProfilePanelClickListener = View.OnClickListener {

        if (::resume.isInitialized){
            val userProfileFragment = UserProfileFragment()
            val bundle = Bundle()

            bundle.putSerializable("user", resume.user)
            userProfileFragment.arguments = bundle

            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.addSharedElement(avatar, avatar.transitionName)
                ?.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
                ?.addToBackStack(null)
                ?.replace(R.id.main_fragment_container, userProfileFragment)
                ?.commit()
        }
    }

    private val onFabClickListener = View.OnClickListener {
        val createAndEditResumeFragment = CreateAndEditResumeFragment()
        val bundle = Bundle()
        bundle.putSerializable("resume", resume)
        createAndEditResumeFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slide_to_left,
                R.anim.slide_from_left,
                R.anim.slide_to_right
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, createAndEditResumeFragment)
            ?.commit()
    }

    private fun getResume(resumeId : String, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getOneResume(resumeId, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Resume> {
            override fun onResponse(call: Call<Resume>, response: Response<Resume>) {
                if (response.isSuccessful){
                    resume = response.body()!!

                    name?.text = resume.name
                    description?.text = resume.description
                    creator_name_and_surname?.text = resume.user.firstName + " " + resume.user.secondName

                    if (resume.user.id == currentUserWithToken.user.id){
                        fab?.show()
                    }
                }  else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }

                swipeRefreshLayout?.isRefreshing = false
            }

            override fun onFailure(call: Call<Resume>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }
}
