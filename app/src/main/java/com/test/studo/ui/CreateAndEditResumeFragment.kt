package com.test.studo.ui


import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.test.studo.R
import com.test.studo.api
import com.test.studo.api.models.Resume
import com.test.studo.api.models.ResumeCreateRequest
import com.test.studo.api.models.ResumeEditRequest
import com.test.studo.compactResumeList
import com.test.studo.currentUserWithToken
import kotlinx.android.synthetic.main.fragment_create_and_edit_resume.*
import kotlinx.android.synthetic.main.fragment_create_and_edit_resume.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAndEditResumeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_and_edit_resume, container, false)

        val resume = arguments?.getSerializable("resume") as Resume?

        if (resume != null){
            view.collapse_toolbar.title = resources.getText(R.string.edit_resume)
            view.input_title.editText?.setText(resume.name)
            view.input_description.editText?.setText(resume.description)
            view.save_fab.setOnClickListener { editResume(resume.id) }

            view.delete_fab.show()
            view.delete_fab.setOnClickListener { deleteResume(resume.id) }
        } else {
            view.collapse_toolbar.title = resources.getText(R.string.create_resume)
            view.save_fab.setOnClickListener { createResume() }
        }

        return view
    }

    private fun isUserDataIsCorrect() : Boolean{
        if (input_title.editText!!.text.isEmpty()){
            input_title.error = resources.getText(R.string.empty_field_error)
            return false
        } else {
            input_title.isErrorEnabled = false
        }
        if (input_description.editText!!.text.isEmpty()){
            input_description.error = resources.getText(R.string.empty_field_error)
            return false
        } else {
            input_title.isErrorEnabled = false
        }

        return true
    }

    private fun createResume(){

        if(!isUserDataIsCorrect()){
            return
        }

        val resumeCreateRequest = ResumeCreateRequest(
            input_title.editText!!.text.toString(),
            input_description.editText!!.text.toString()
        )

        api.createResume(resumeCreateRequest, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Resume> {
            override fun onResponse(call: Call<Resume>, response: Response<Resume>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, resources.getText(R.string.create_resume_success), Toast.LENGTH_LONG).show()
                    compactResumeList = null
                    activity?.supportFragmentManager?.popBackStack()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Resume>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun editResume(resumeId : String){

        if (!isUserDataIsCorrect()){
            return
        }

        val resumeEditRequest = ResumeEditRequest(
            resumeId,
            input_title.editText!!.text.toString(),
            input_description.editText!!.text.toString()
        )

        api.editResume(resumeEditRequest, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Resume> {
            override fun onResponse(call: Call<Resume>, response: Response<Resume>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, resources.getText(R.string.edit_resume_success), Toast.LENGTH_LONG).show()
                    compactResumeList = null
                    activity?.supportFragmentManager?.popBackStack()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Resume>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteResume(resumeId : String){

        val onPositiveButtonClick = { _: DialogInterface, _: Int ->
            api.deleteResume(resumeId, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, resources.getText(R.string.delete_resume_success), Toast.LENGTH_LONG).show()
                        compactResumeList = null

                        with(activity?.supportFragmentManager){
                            this?.popBackStack()
                            this?.popBackStack()
                        }
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null) {
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                }
            })
        }

        AlertDialog.Builder(context!!)
            .setTitle(resources.getText(R.string.delete_resume_confirmation))
            .setCancelable(true)
            .setNegativeButton(resources.getText(R.string.cancel), null)
            .setPositiveButton(resources.getText(R.string.ok), DialogInterface.OnClickListener(function = onPositiveButtonClick))
            .show()

    }
}