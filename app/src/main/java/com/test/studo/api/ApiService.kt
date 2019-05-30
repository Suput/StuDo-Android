package com.test.studo.api

import com.test.studo.api.models.UserRequest
import com.test.studo.api.models.UserResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    fun login(@Body body : UserRequest ) : Call<UserResponse>

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