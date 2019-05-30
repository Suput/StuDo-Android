package com.test.studo.ui

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.test.studo.R
import com.test.studo.api.models.UserRequest
import com.test.studo.api.models.UserResponse
import com.test.studo.api.models.userResponse
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = android.R.attr.windowBackground
        }

        setContentView(R.layout.activity_sign_in)

        btn_login.setOnClickListener {
            val userRequest = UserRequest(input_email.text.toString(), input_password.text.toString())

            api.login(userRequest).enqueue(object : Callback<UserResponse> {

                // Successful connection with server
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {

                    // Valid log/pass pair
                    if (response.isSuccessful) {
                        userResponse = response.body()
                        Toast.makeText(this@SignInActivity, "Hello, " + userResponse?.user?.firstName + " " + userResponse?.user?.surname, Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                        // Other errors
                    } else {
                        Toast.makeText(this@SignInActivity, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }

                // No connection with server
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@SignInActivity, "FAIL: " + t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
