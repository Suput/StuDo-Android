package com.test.studo.api

import com.test.studo.api.models.ChangePasswordRequest
import com.test.studo.api.models.UserLoginRequest
import com.test.studo.api.models.UserLoginResponse
import com.test.studo.api.models.UserRegistrationRequest
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    fun login(@Body body : UserLoginRequest ) : Call<UserLoginResponse>

    @POST("api/auth/register")
    fun registration(@Body body : UserRegistrationRequest) : Call<Void>

    @POST("api/user/password/change")
    fun changePassword(@Body body : ChangePasswordRequest, @Header("Authorization") accessToken : String) : Call<Void>

    companion object Factory {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://dev.studo.rtuitlab.ru/")
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}