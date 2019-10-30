package com.test.studo.ui

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.test.studo.R
import com.test.studo.api.models.UserLoginRequest
import com.test.studo.api.models.UserLoginResponse
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import android.content.res.Configuration
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import com.google.gson.Gson
import com.test.studo.api
import com.test.studo.api.models.ResetPasswordRequest


class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.statusBarColor = android.R.attr.windowBackground
        }

        setContentView(R.layout.activity_sign_in)

        login_btn.setOnClickListener{ login() }

        reset_password_btn.setOnClickListener{ showResetPasswordDialog() }

        link_sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun login(){
        val userLoginRequest = UserLoginRequest(input_email.editText?.text.toString(), input_password.editText?.text.toString())

        // TODO: Add user data check

        api.login(userLoginRequest).enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                if (response.isSuccessful) {
                    val shared = getSharedPreferences("StuDoShared", Context.MODE_PRIVATE)
                    val editor = shared.edit()
                    editor.putString("userWithToken", Gson().toJson(response.body())).apply()
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null && errorBodyText.isNotEmpty()){
                        Toast.makeText(this@SignInActivity, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@SignInActivity, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                Toast.makeText(this@SignInActivity, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun resetPassword(email : TextInputLayout, resetPasswordAlert : AlertDialog){

        val resetPasswordRequest = ResetPasswordRequest(email.editText?.text.toString())

        // TODO: Add user data check

        api.resetPassword(resetPasswordRequest).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    Toast.makeText(this@SignInActivity, resources.getString(R.string.check_email_to_reset), Toast.LENGTH_LONG).show()
                    resetPasswordAlert.dismiss()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null && errorBodyText.isNotEmpty()){
                        Toast.makeText(this@SignInActivity, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@SignInActivity, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@SignInActivity, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showResetPasswordDialog(){
        val resetPasswordView = this.layoutInflater.inflate(R.layout.dialog_reset_password, null)

        val email = resetPasswordView!!.findViewById(R.id.input_email_for_reset) as TextInputLayout

        val builder = AlertDialog.Builder(this)
            .setView(resetPasswordView)
            .setTitle(resources.getString(R.string.reset_password))
            .setCancelable(true)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.ok), null)

        val resetPasswordAlert = builder.create()
        resetPasswordAlert.show()
        resetPasswordAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            resetPassword(email, resetPasswordAlert)
        }
    }
}

