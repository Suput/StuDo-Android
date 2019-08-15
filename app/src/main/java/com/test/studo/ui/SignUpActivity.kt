package com.test.studo.ui

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.test.studo.R
import com.test.studo.api.models.UserRegistrationRequest
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = android.R.attr.windowBackground
        }

        setContentView(R.layout.activity_sign_up)

        btn_sign_up.setOnClickListener{
            val userRegisterRequest = UserRegistrationRequest(input_first_name.text.toString(), input_second_name.text.toString(),
                input_email.text.toString(),input_card_number.text.toString(), input_password.text.toString(),
                input_confirm_password.text.toString())


            api.registration(userRegisterRequest).enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        Toast.makeText(this@SignUpActivity, "Check your email to activate account", Toast.LENGTH_LONG).show()
                        goToSignIn(null)
                    } else {
                        if (response.errorBody() != null){
                            Toast.makeText(this@SignUpActivity, response.errorBody()?.string(), Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@SignUpActivity, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "FAIL: " + t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    fun goToSignIn(v : View?) {
        startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        finish()
    }
}
