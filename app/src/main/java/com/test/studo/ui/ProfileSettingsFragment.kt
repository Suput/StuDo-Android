package com.test.studo.ui

import android.content.Context
import android.content.Intent
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

        view.input_first_name.editText?.setText(currentUserWithToken.user.firstName)
        view.input_second_name.editText?.setText(currentUserWithToken.user.secondName)
        currentUserWithToken.user.studentCardNumber?.let {
            view.input_card_number.editText?.setText(currentUserWithToken.user.studentCardNumber)
        }

        view.input_first_name.editText?.addTextChangedListener(onTextChangedListener)
        view.input_second_name.editText?.addTextChangedListener(onTextChangedListener)
        view.input_card_number.editText?.addTextChangedListener(onTextChangedListener)

        view.change_email_btn.setOnClickListener(onChangeEmailClickListener)
        view.change_password_btn.setOnClickListener(onChangePasswordClickListener)
        view.log_out_btn.setOnClickListener(onLogOutButtonClick)

        return view
    }

    private val onLogOutButtonClick = View.OnClickListener {
        this.activity?.getSharedPreferences("shared", Context.MODE_PRIVATE)?.edit()?.clear()?.apply()

        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }

    private val onChangePasswordClickListener = View.OnClickListener {
        val changePasswordView = activity!!.layoutInflater.inflate(R.layout.dialog_change_password, null)

        val oldPassword = changePasswordView!!.findViewById(R.id.input_old_password) as TextInputLayout
        val newPassword = changePasswordView.findViewById(R.id.input_new_password) as TextInputLayout
        val confirmNewPassword = changePasswordView.findViewById(R.id.input_confirm_new_password) as TextInputLayout

        val builder = AlertDialog.Builder(context!!)
            .setView(changePasswordView)
            .setTitle(resources.getText(R.string.change_password))
            .setCancelable(true)
            .setNegativeButton(resources.getText(R.string.cancel), null)
            .setPositiveButton(resources.getText(R.string.ok), null)

        val changePasswordAlert = builder.create()
        changePasswordAlert.show()
        changePasswordAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            changePassword(oldPassword, newPassword, confirmNewPassword)
        }
    }

    private fun changePassword(oldPassword : TextInputLayout, newPassword : TextInputLayout, confirmNewPassword : TextInputLayout){
        if (oldPassword.editText?.text.toString().length < 6){
            oldPassword.error = resources.getText(R.string.wrong_password_error).toString()
            return
        } else {
            oldPassword.isErrorEnabled = false
        }

        if (newPassword.editText?.text.toString().length < 6){
            newPassword.error = resources.getText(R.string.wrong_password_error).toString()
            return
        } else {
            newPassword.isErrorEnabled = false
        }

        if (confirmNewPassword.editText?.text.toString().length < 6){
            confirmNewPassword.error = resources.getText(R.string.wrong_password_error).toString()
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
            currentUserWithToken.user.id,
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
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private val onChangeEmailClickListener = View.OnClickListener {
        val changeEmailView = activity!!.layoutInflater.inflate(R.layout.dialog_change_email, null)

        val oldEmail = changeEmailView!!.findViewById(R.id.input_old_email) as TextInputLayout
        val newEmail = changeEmailView.findViewById(R.id.input_new_email) as TextInputLayout
        val confirmNewEmail = changeEmailView.findViewById(R.id.input_confirm_new_email) as TextInputLayout

        val builder = AlertDialog.Builder(context!!)
            .setView(changeEmailView)
            .setTitle(resources.getText(R.string.change_email))
            .setCancelable(true)
            .setNegativeButton(resources.getText(R.string.cancel), null)
            .setPositiveButton(resources.getText(R.string.ok), null)

        val changeEmailAlert = builder.create()
        changeEmailAlert.show()
        changeEmailAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            changeEmail(oldEmail, newEmail, confirmNewEmail, changeEmailAlert)
        }
    }

    private fun changeEmail(oldEmail : TextInputLayout, newEmail : TextInputLayout, confirmNewEmail : TextInputLayout, changeEmailAlert : AlertDialog){
        if (oldEmail.editText?.text.toString().isEmpty()){
            oldEmail.error = resources.getText(R.string.wrong_email_error).toString()
            return
        } else {
            oldEmail.isErrorEnabled = false
        }

        if (newEmail.editText?.text.toString().isEmpty()){
            newEmail.error = resources.getText(R.string.wrong_email_error).toString()
            return
        } else {
            newEmail.isErrorEnabled = false
        }

        if (confirmNewEmail.editText?.text.toString().isEmpty()){
            confirmNewEmail.error = resources.getText(R.string.wrong_email_error).toString()
            return
        } else {
            confirmNewEmail.isErrorEnabled = false
        }

        if (newEmail.editText?.text.toString() != confirmNewEmail.editText?.text.toString()){
            newEmail.error = resources.getText(R.string.equal_email_error).toString()
            confirmNewEmail.error = resources.getText(R.string.equal_email_error).toString()
            return
        } else {
            newEmail.isErrorEnabled = false
            confirmNewEmail.isErrorEnabled = false
        }

        if (oldEmail.editText?.text.toString() == newEmail.editText?.text.toString()){
            oldEmail.error = resources.getText(R.string.same_email_error).toString()
            newEmail.error = resources.getText(R.string.same_email_error).toString()
            return
        } else {
            oldEmail.isErrorEnabled = false
            newEmail.isErrorEnabled = false
        }

        val changeEmailRequest = ChangeEmailRequest(
            currentUserWithToken.user.id,
            oldEmail.editText?.text.toString(),
            newEmail.editText?.text.toString()
        )

        api.changeEmail(changeEmailRequest, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    changeEmailAlert.cancel()
                    Toast.makeText(context, resources.getText(R.string.new_email_verification), Toast.LENGTH_LONG).show()
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
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private val onTextChangedListener = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            if (
                input_first_name.editText?.text.toString() != currentUserWithToken.user.firstName ||
                input_second_name.editText?.text.toString() != currentUserWithToken.user.secondName ||
                input_card_number.editText?.text.toString() != currentUserWithToken.user.studentCardNumber
            ){
                save_fab.show()
                save_fab.setOnClickListener { changeUserInfo() }
            } else {
                save_fab.hide()
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
    }

    private fun isUserDataIsCorrect() : Boolean{
        if (input_first_name.editText!!.text.isEmpty()){
            input_first_name.error = resources.getText(R.string.empty_field_error)
            return false
        } else {
            input_first_name.isErrorEnabled = false
        }
        if (input_second_name.editText!!.text.isEmpty()){
            input_second_name.error = resources.getText(R.string.empty_field_error)
            return false
        } else {
            input_second_name.isErrorEnabled = false
        }
        if (input_card_number.editText!!.text.isEmpty()){
            input_card_number.error = resources.getText(R.string.empty_field_error)
            return false
        } else {
            input_card_number.isErrorEnabled = false
        }

        return true
    }

    private fun changeUserInfo(){
        if (!isUserDataIsCorrect()){
            return
        }

        //TODO : add user data check

        val changeUserInfoRequest = ChangeUserInfoRequest(
            currentUserWithToken.user.id,
            input_card_number.editText?.text.toString(),
            input_first_name.editText?.text.toString(),
            input_second_name.editText?.text.toString()
        )

        api.changeUserInfo(changeUserInfoRequest, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful){
                    Toast.makeText(context, resources.getText(R.string.changes_saved), Toast.LENGTH_LONG).show()
                    currentUserWithToken.user = response.body()!!
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }
}