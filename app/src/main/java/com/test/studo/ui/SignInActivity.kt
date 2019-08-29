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

        login_btn.setOnClickListener(onLoginButtonClickListener)

        reset_password_btn.setOnClickListener(onResetButtonClickListener)

        link_sign_up.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            finish()
        }
    }

    private val onLoginButtonClickListener = View.OnClickListener{
        val userLoginRequest = UserLoginRequest(input_email.editText?.text.toString(), input_password.editText?.text.toString())

        // TODO: Add user data check

        api.login(userLoginRequest).enqueue(object : Callback<UserLoginResponse> {

            // Successful connection with server
            override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {

                // Valid log/pass pair
                if (response.isSuccessful) {
                    val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
                    val editor = shared.edit()
                    editor.putString("userWithToken", Gson().toJson(response.body())).apply()

                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                } else { // Other errors
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(this@SignInActivity, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@SignInActivity, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            // resources.getText(R.string.connection_with_server_error)
            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                Toast.makeText(this@SignInActivity, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private val onResetButtonClickListener = View.OnClickListener {

        val resetPasswordView = this.layoutInflater.inflate(R.layout.dialog_reset_password, null)
        val email = resetPasswordView!!.findViewById(R.id.input_email_for_reset) as TextInputLayout

        val builder = AlertDialog.Builder(this)
            .setView(resetPasswordView)
            .setTitle(resources.getText(R.string.reset_password))
            .setCancelable(true)
            .setNegativeButton(resources.getText(R.string.cancel), null)
            .setPositiveButton(resources.getText(R.string.ok), null)

        val resetPasswordAlert = builder.create()
        resetPasswordAlert.show()
        resetPasswordAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            resetPassword(email, resetPasswordAlert)
        }
    }

    private fun resetPassword(email : TextInputLayout, resetPasswordAlert : AlertDialog){

        val resetPasswordRequest = ResetPasswordRequest(email.editText?.text.toString())

        // TODO: Add user data check

        api.resetPassword(resetPasswordRequest).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    Toast.makeText(this@SignInActivity, "Check your email to change password", Toast.LENGTH_LONG).show()
                    resetPasswordAlert.dismiss()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(this@SignInActivity, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@SignInActivity, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@SignInActivity, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }
}

