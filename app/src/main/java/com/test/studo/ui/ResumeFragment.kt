package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
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
import java.text.SimpleDateFormat

class ResumeFragment : Fragment() {

    lateinit var resume : Resume

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        val bundle = this.arguments
        val adId = bundle?.getString("resumeId")
        adId?.let { getResume(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_resume, container, false)

        view.collapse_toolbar.title = resources.getText(R.string.ad)

        val bundle = this.arguments
        view.name.text = bundle?.getString("name")
        view.separator_1.visibility = View.VISIBLE
        view.separator_2.visibility = View.VISIBLE

        return view
    }

    private fun getResume(resumeId : String){
        api.getOneResume(resumeId, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Resume> {
            override fun onResponse(call: Call<Resume>, response: Response<Resume>) {
                if (response.isSuccessful){
                    resume = response.body()!!

                    name.text = resume.name
                    description.text = resume.description
                    creator_name.text = resume.user.firstName + " " + resume.user.secondName
                }  else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Resume>, t: Throwable) {
                Toast.makeText(context, "No connection with server", Toast.LENGTH_LONG).show()
            }
        })
    }
}
