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
import com.test.studo.api.models.Organization
import com.test.studo.api.models.OrganizationCreateRequest
import com.test.studo.api.models.OrganizationEditRequest
import com.test.studo.currentUserWithToken
import com.test.studo.organizationList
import kotlinx.android.synthetic.main.fragment_create_and_edit_organization.*
import kotlinx.android.synthetic.main.fragment_create_and_edit_organization.view.*
import kotlinx.android.synthetic.main.fragment_create_and_edit_organization.view.input_description
import kotlinx.android.synthetic.main.fragment_create_and_edit_organization.view.input_title
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAndEditOrganizationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_and_edit_organization, container, false)

        view.collapse_toolbar.title = resources.getText(R.string.create_organization)

        val organization = arguments?.getSerializable("organization") as Organization?

        if (organization != null){
            view.collapse_toolbar.title = resources.getText(R.string.edit_resume)
            view.input_title.editText?.setText(organization.name)
            view.input_description.editText?.setText(organization.description)
            view.save_fab.setOnClickListener { editOrganization(organization.id) }

            view.delete_fab.show()
            view.delete_fab.setOnClickListener { deleteOrganization(organization.id) }
        } else {
            view.collapse_toolbar.title = resources.getText(R.string.create_resume)
            view.save_fab.setOnClickListener { createOrganization() }
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

    private fun createOrganization(){

        if(!isUserDataIsCorrect()){
            return
        }

        val organizationCreateRequest = OrganizationCreateRequest(
            input_title.editText!!.text.toString(),
            input_description.editText!!.text.toString()
        )

        api.createOrganization(organizationCreateRequest, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, resources.getText(R.string.create_organization_success), Toast.LENGTH_LONG).show()
                    organizationList = null
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

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun editOrganization(organizationId : String){

        if (!isUserDataIsCorrect()){
            return
        }

        val organizationEditRequest = OrganizationEditRequest(
            organizationId,
            input_title.editText!!.text.toString(),
            input_description.editText!!.text.toString()
        )

        api.editOrganization(organizationEditRequest, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, resources.getText(R.string.edit_organization_success), Toast.LENGTH_LONG).show()
                    organizationList = null
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

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteOrganization(organizationId : String){

        val onPositiveButtonClick = { _: DialogInterface, _: Int ->
            api.deleteOrganization(organizationId, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, resources.getText(R.string.delete_organization_success), Toast.LENGTH_LONG).show()
                        organizationList = null

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
            .setTitle(resources.getText(R.string.delete_organization_confirmation))
            .setCancelable(true)
            .setNegativeButton(resources.getText(R.string.cancel), null)
            .setPositiveButton(resources.getText(R.string.ok), DialogInterface.OnClickListener(function = onPositiveButtonClick))
            .show()
    }
}