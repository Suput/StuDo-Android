package com.test.studo.ui

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.studo.R
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_profile_settings.view.*
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.widget.Toast
import com.test.studo.api
import com.test.studo.api.models.ChangeEmailRequest
import com.test.studo.api.models.ChangePasswordRequest
import com.test.studo.api.models.ChangeUserInfoRequest
import com.test.studo.api.models.User
import com.test.studo.currentUserWithToken
import com.test.studo.isEmail
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

        view.collapse_toolbar.title = resources.getString(R.string.profile)

        currentUserWithToken.user = arguments!!.getSerializable("user") as User

        view.input_first_name.editText?.setText(currentUserWithToken.user.firstName)
        view.input_second_name.editText?.setText(currentUserWithToken.user.secondName)
        currentUserWithToken.user.studentCardNumber?.let {
            view.input_card_number.editText?.setText(currentUserWithToken.user.studentCardNumber)
        }
        
        view.input_first_name.editText?.addTextChangedListener(onUserInfoChangedListener)
        view.input_second_name.editText?.addTextChangedListener(onUserInfoChangedListener)
        view.input_card_number.editText?.addTextChangedListener(onUserInfoChangedListener)

        view.change_email_btn.setOnClickListener{ showChangeEmailDialog() }
        view.change_password_btn.setOnClickListener{ showChangePasswordDialog() }
        view.log_out_btn.setOnClickListener{ logOut() }

        return view
    }

    private fun changeUserInfo(){
        val changeUserInfoRequest = ChangeUserInfoRequest(
            currentUserWithToken.user.id,
            input_card_number.editText?.text.toString(),
            input_first_name.editText?.text.toString(),
            input_second_name.editText?.text.toString()
        )

        if (!isRequestIsCorrect(changeUserInfoRequest)){
            return
        }

        api.changeUserInfo(changeUserInfoRequest, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<User>{
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful){
                        Toast.makeText(context, resources.getString(R.string.changes_saved), Toast.LENGTH_LONG).show()
                        currentUserWithToken.user = response.body()!!
                        save_account_info_fab.hide()
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()){
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun changeEmail(oldEmail : TextInputLayout, newEmail : TextInputLayout, confirmNewEmail : TextInputLayout, changeEmailAlert : AlertDialog){
        val changeEmailRequest = ChangeEmailRequest(
            currentUserWithToken.user.id,
            oldEmail.editText?.text.toString(),
            newEmail.editText?.text.toString()
        )

        if (!isRequestIsCorrect(changeEmailRequest, oldEmail, newEmail, confirmNewEmail)){
            return
        }

        api.changeEmail(changeEmailRequest, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        changeEmailAlert.dismiss()
                        Toast.makeText(context, resources.getString(R.string.new_email_verification), Toast.LENGTH_LONG).show()
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()){
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun changePassword(oldPassword : TextInputLayout, newPassword : TextInputLayout, confirmNewPassword : TextInputLayout){
        val changePasswordRequest = ChangePasswordRequest(
            currentUserWithToken.user.id,
            oldPassword.editText?.text.toString(),
            newPassword.editText?.text.toString()
        )

        if (!isRequestIsCorrect(changePasswordRequest, oldPassword, newPassword, confirmNewPassword)){
            return
        }

        api.changePassword(changePasswordRequest, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    log_out_btn.performClick()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null && errorBodyText.isNotEmpty()){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showChangeEmailDialog(){
        val changeEmailView = activity!!.layoutInflater.inflate(R.layout.dialog_change_email, null)

        val oldEmail = changeEmailView!!.findViewById(R.id.input_old_email) as TextInputLayout
        val newEmail = changeEmailView.findViewById(R.id.input_new_email) as TextInputLayout
        val confirmNewEmail = changeEmailView.findViewById(R.id.input_confirm_new_email) as TextInputLayout

        val builder = AlertDialog.Builder(context!!)
            .setView(changeEmailView)
            .setTitle(resources.getString(R.string.change_email))
            .setCancelable(true)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.ok), null)

        val changeEmailAlert = builder.create()
        changeEmailAlert.show()
        changeEmailAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            changeEmail(oldEmail, newEmail, confirmNewEmail, changeEmailAlert)
        }
    }

    private fun showChangePasswordDialog(){
        val changePasswordView = activity!!.layoutInflater.inflate(R.layout.dialog_change_password, null)

        val oldPassword = changePasswordView!!.findViewById(R.id.input_old_password) as TextInputLayout
        val newPassword = changePasswordView.findViewById(R.id.input_new_password) as TextInputLayout
        val confirmNewPassword = changePasswordView.findViewById(R.id.input_confirm_new_password) as TextInputLayout

        val builder = AlertDialog.Builder(context!!)
            .setView(changePasswordView)
            .setTitle(resources.getString(R.string.change_password))
            .setCancelable(true)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.ok), null)

        val changePasswordAlert = builder.create()
        changePasswordAlert.show()
        changePasswordAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            changePassword(oldPassword, newPassword, confirmNewPassword)
        }
    }

    private val onUserInfoChangedListener = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            if (
                input_first_name.editText?.text.toString() != currentUserWithToken.user.firstName ||
                input_second_name.editText?.text.toString() != currentUserWithToken.user.secondName ||
                input_card_number.editText?.text.toString() != currentUserWithToken.user.studentCardNumber
            ){
                save_account_info_fab.show()
                save_account_info_fab.setOnClickListener { changeUserInfo() }
            } else {
                save_account_info_fab.hide()
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
    }

    private fun isRequestIsCorrect(request : Any,
                                   old : TextInputLayout = TextInputLayout(context),
                                   new : TextInputLayout = TextInputLayout(context),
                                   confirm : TextInputLayout = TextInputLayout(context)) : Boolean{
        when(request){

            is ChangeUserInfoRequest -> {
                if (input_first_name.editText!!.text.isEmpty()){
                    input_first_name.error = resources.getString(R.string.empty_field_error)
                    return false
                } else {
                    input_first_name.isErrorEnabled = false
                }
                if (input_second_name.editText!!.text.isEmpty()){
                    input_second_name.error = resources.getString(R.string.empty_field_error)
                    return false
                } else {
                    input_second_name.isErrorEnabled = false
                }
                if (input_card_number.editText!!.text.isEmpty()){
                    input_card_number.error = resources.getString(R.string.empty_field_error)
                    return false
                } else {
                    input_card_number.isErrorEnabled = false
                }
            }

            is ChangeEmailRequest -> {
                if (old.editText?.text.toString().isEmpty() || !old.editText?.text.toString().isEmail()){
                    old.error = resources.getString(R.string.wrong_email_error).toString()
                    return false
                } else {
                    old.isErrorEnabled = false
                }

                if (new.editText?.text.toString().isEmpty() || !new.editText?.text.toString().isEmail()){
                    new.error = resources.getString(R.string.wrong_email_error).toString()
                    return false
                } else {
                    new.isErrorEnabled = false
                }

                if (confirm.editText?.text.toString().isEmpty() || !confirm.editText?.text.toString().isEmail()){
                    confirm.error = resources.getString(R.string.wrong_email_error).toString()
                    return false
                } else {
                    confirm.isErrorEnabled = false
                }

                if (new.editText?.text.toString() != confirm.editText?.text.toString()){
                    new.error = resources.getString(R.string.equal_email_error).toString()
                    confirm.error = resources.getString(R.string.equal_email_error).toString()
                    return false
                } else {
                    new.isErrorEnabled = false
                    confirm.isErrorEnabled = false
                }

                if (old.editText?.text.toString() == new.editText?.text.toString()){
                    old.error = resources.getString(R.string.same_email_error).toString()
                    new.error = resources.getString(R.string.same_email_error).toString()
                    return false
                } else {
                    old.isErrorEnabled = false
                    new.isErrorEnabled = false
                }
            }

            is ChangePasswordRequest -> {
                if (old.editText?.text.toString().length < 6){
                    old.error = resources.getString(R.string.wrong_password_error).toString()
                    return false
                } else {
                    old.isErrorEnabled = false
                }

                if (new.editText?.text.toString().length < 6){
                    new.error = resources.getString(R.string.wrong_password_error).toString()
                    return false
                } else {
                    new.isErrorEnabled = false
                }

                if (confirm.editText?.text.toString().length < 6){
                    confirm.error = resources.getString(R.string.wrong_password_error).toString()
                    return false
                } else {
                    confirm.isErrorEnabled = false
                }

                if (new.editText?.text.toString() != confirm.editText?.text.toString()){
                    new.error = resources.getString(R.string.equal_password_error).toString()
                    confirm.error = resources.getString(R.string.equal_password_error).toString()
                    return false
                } else {
                    new.isErrorEnabled = false
                    confirm.isErrorEnabled = false
                }

                if (old.editText?.text.toString() == new.editText?.text.toString()){
                    old.error = resources.getString(R.string.same_password_error).toString()
                    new.error = resources.getString(R.string.same_password_error).toString()
                    return false
                } else {
                    old.isErrorEnabled = false
                    new.isErrorEnabled = false
                }
            }
        }

        return true
    }

    private fun logOut(){
        activity?.getSharedPreferences("shared", Context.MODE_PRIVATE)?.edit()?.clear()?.apply()
        activity?.recreate()
    }
}