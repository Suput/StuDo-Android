package com.test.studo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.studo.R
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_profile_settings.view.*
import android.support.v7.app.AlertDialog
import android.transition.TransitionInflater
import android.widget.Toast
import com.test.studo.api
import com.test.studo.api.models.ChangePasswordRequest
import com.test.studo.currentUserWithToken
import kotlinx.android.synthetic.main.fragment_profile_settings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileSettingsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile_settings, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.title_profile)

        view.input_first_name.setText(currentUserWithToken.user.firstName)
        view.input_second_name.setText(currentUserWithToken.user.secondName)
        currentUserWithToken.user.studentCardNumber?.let {
            view.input_card_number.setText(currentUserWithToken.user.studentCardNumber)
        }

        view.log_out_btn.setOnClickListener(onLogOutButtonClick)
        view.change_password_btn.setOnClickListener(onChangePasswordClickListener)

        return view
    }

    private val onLogOutButtonClick = View.OnClickListener {
        this.activity?.getSharedPreferences("shared", Context.MODE_PRIVATE)?.edit()?.clear()?.apply()

        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }

    private val onChangePasswordClickListener = View.OnClickListener {
        val builder = AlertDialog.Builder(context!!)
        val changePasswordView = activity!!.layoutInflater.inflate(R.layout.dialog_change_password, null)
        val oldPassword = changePasswordView!!.findViewById(R.id.input_old_password) as TextInputLayout
        val newPassword = changePasswordView.findViewById(R.id.input_new_password) as TextInputLayout
        val confirmNewPassword = changePasswordView.findViewById(R.id.input_confirm_new_password) as TextInputLayout

        builder.setView(changePasswordView)
            .setTitle(resources.getText(R.string.change_password).toString())
            .setCancelable(true)
            .setNegativeButton(resources.getText(R.string.cancel).toString(), null)
            .setPositiveButton(resources.getText(R.string.ok).toString(), null)

        val changePasswordAlert = builder.create()
        changePasswordAlert.show()
        changePasswordAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                changePassword(oldPassword, newPassword, confirmNewPassword)
            }
        }

    private fun changePassword(oldPassword : TextInputLayout, newPassword : TextInputLayout, confirmNewPassword : TextInputLayout){
        if (oldPassword.editText?.text.toString().length < 6){
            oldPassword.error = resources.getText(R.string.empty_password_error).toString()
            return
        } else {
            oldPassword.isErrorEnabled = false
        }

        if (newPassword.editText?.text.toString().length < 6){
            newPassword.error = resources.getText(R.string.empty_password_error).toString()
            return
        } else {
            newPassword.isErrorEnabled = false
        }

        if (confirmNewPassword.editText?.text.toString().length < 6){
            confirmNewPassword.error = resources.getText(R.string.empty_password_error).toString()
            return
        } else {
            confirmNewPassword.isErrorEnabled = false
        }

        if (newPassword.editText?.text.toString() != confirmNewPassword.editText?.text.toString()){
            newPassword.error = resources.getText(R.string.equal_password_error).toString()
            confirmNewPassword.error = resources.getText(R.string.equal_password_error).toString()
            return
        } else {
            newPassword.isErrorEnabled = false
            confirmNewPassword.isErrorEnabled = false
        }

        if (oldPassword.editText?.text.toString() == newPassword.editText?.text.toString()){
            oldPassword.error = resources.getText(R.string.same_password_error).toString()
            newPassword.error = resources.getText(R.string.same_password_error).toString()
            return
        } else {
            oldPassword.isErrorEnabled = false
            newPassword.isErrorEnabled = false
        }

        val changePasswordRequest = ChangePasswordRequest(
            currentUserWithToken.user.email,
            oldPassword.editText?.text.toString(),
            newPassword.editText?.text.toString()
        )

        api.changePassword(changePasswordRequest, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    log_out_btn.performClick()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "resources.getText(R.string.connection_with_server_error)", Toast.LENGTH_LONG).show()
            }
        })
    }
}