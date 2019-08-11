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
import com.google.gson.Gson


class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = android.R.attr.windowBackground
        }

        setContentView(R.layout.activity_sign_in)

        btn_login.setOnClickListener {
            val userLoginRequest = UserLoginRequest(input_email.text.toString(), input_password.text.toString())

            api.login(userLoginRequest).enqueue(object : Callback<UserLoginResponse> {

                // Successful connection with server
                override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {

                    // Valid log/pass pair
                    if (response.isSuccessful) {
                        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
                        val editor = shared.edit()
                        editor.putString("userWithToken", Gson().toJson(response.body()))
                        editor.apply()

                        val userWithToken = Gson().fromJson(shared.getString("userWithToken", ""), UserLoginResponse::class.java)

                        Toast.makeText(this@SignInActivity, "Hello, " + userWithToken?.user?.firstName + " " + userWithToken?.user?.secondName, Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                    } else { // Other errors
                        Toast.makeText(this@SignInActivity, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }

                // No connection with server
                override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                    Toast.makeText(this@SignInActivity, "FAIL: " + t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    fun goToSignUp(v : View?) {
        startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        finish()
    }
}

