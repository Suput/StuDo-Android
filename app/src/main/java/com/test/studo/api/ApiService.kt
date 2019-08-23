package com.test.studo.api

import com.test.studo.api.models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    fun login(@Body body : UserLoginRequest ) : Call<UserLoginResponse>

    @POST("auth/register")
    fun registration(@Body body : UserRegistrationRequest) : Call<Void>

    @POST("user/password/change")
    fun changePassword(@Body body : ChangePasswordRequest, @Header("Authorization") accessToken : String) : Call<Void>

    @POST("user/password/reset")
    fun resetPassword(@Body body : ResetPasswordRequest) : Call<Void>

    @GET("ad")
    fun getAllAds(@Header("Authorization") accessToken : String) : Call<List<CompactAd>>

    @GET("resumes")
    fun getAllResumes(@Header("Authorization") accessToken : String) : Call<List<CompactResume>>


    companion object Factory {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://dev.studo.rtuitlab.ru/api/")
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}