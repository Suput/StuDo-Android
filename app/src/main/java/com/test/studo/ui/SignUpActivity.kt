package com.test.studo.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.test.studo.R
import com.test.studo.api
import com.test.studo.api.models.UserRegistrationRequest
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.statusBarColor = android.R.attr.windowBackground
        }

        setContentView(R.layout.activity_sign_up)

        sign_up_btn.setOnClickListener(onSignUpButtonClickListener)

        link_login.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            finish()
        }
    }

    private val onSignUpButtonClickListener = View.OnClickListener {
        val userRegistrationRequest = UserRegistrationRequest(
            input_first_name.editText?.text.toString(),
            input_second_name.editText?.text.toString(),
            input_email.editText?.text.toString(),
            input_card_number.editText?.text.toString(),
            input_password.editText?.text.toString(),
            input_confirm_password.editText?.text.toString()
        )

        // TODO: Add user data check

        if (userRegistrationRequest.password != userRegistrationRequest.passwordConfirm){
            input_password.error = resources.getText(R.string.equal_password_error).toString()
            input_confirm_password.error = resources.getText(R.string.equal_password_error).toString()
        } else {
            api.registration(userRegistrationRequest).enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        Toast.makeText(this@SignUpActivity, resources.getText(R.string.email_verification), Toast.LENGTH_LONG).show()
                        link_login.performClick()
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null){
                            Toast.makeText(this@SignUpActivity, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@SignUpActivity, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
