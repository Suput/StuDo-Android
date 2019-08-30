package com.test.studo.api

import com.test.studo.api.models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    fun login(@Body body : UserLoginRequest ) :
            Call<UserLoginResponse>

    @POST("auth/register")
    fun registration(@Body body : UserRegistrationRequest) :
            Call<Void>



    @POST("user/password/change")
    fun changePassword(@Body body : ChangePasswordRequest, @Header("Authorization") accessToken : String) :
            Call<Void>

    @POST("user/password/reset")
    fun resetPassword(@Body body : ResetPasswordRequest) :
            Call<Void>



    @POST("user/change/info")
    fun changeUserInfo(@Body body : ChangeUserInfoRequest, @Header("Authorization") accessToken : String) :
            Call<User>

    @POST("user/change/email")
    fun changeEmail(@Body body : ChangeEmailRequest, @Header("Authorization") accessToken : String) :
            Call<Void>



    @GET("ad")
    fun getAllAds(@Header("Authorization") accessToken : String) :
            Call<List<CompactAd>>

    @GET("ad/user/{userId}")
    fun getUserAds(@Path("userId") userId : String, @Header("Authorization") accessToken : String) :
            Call<List<CompactAd>>

    @GET("ad/{adId}")
    fun getOneAd(@Path("adId") adId : String, @Header("Authorization") accessToken : String) :
            Call<Ad>

    @POST("ad")
    fun createAd(@Body body : AdCreateRequest, @Header("Authorization") accessToken : String) :
            Call<Ad>

    @PUT("ad")
    fun editAd(@Body body : AdEditRequest, @Header("Authorization") accessToken : String) :
            Call<Ad>

    @DELETE("ad/{adId}")
    fun deleteAd(@Path("adId") adId : String, @Header("Authorization") accessToken : String) :
            Call<String>



    @GET("resumes")
    fun getAllResumes(@Header("Authorization") accessToken : String) :
            Call<List<CompactResume>>

    @GET("resumes/user/{userId}")
    fun getUserResumes(@Path("userId") userId : String, @Header("Authorization") accessToken : String) :
            Call<List<CompactResume>>

    @GET("resumes/{resumeId}")
    fun getOneResume(@Path("resumeId") resumeId : String, @Header("Authorization") accessToken : String) :
            Call<Resume>

    @POST("resumes")
    fun createResume(@Body body : ResumeCreateRequest, @Header("Authorization") accessToken : String) :
            Call<Resume>

    @PUT("resumes")
    fun editResume(@Body body : ResumeEditRequest, @Header("Authorization") accessToken : String) :
            Call<Resume>

    @DELETE("resumes/{resumeId}")
    fun deleteResume(@Path("resumeId") resumeId : String, @Header("Authorization") accessToken : String) :
            Call<String>



    @GET("organization")
    fun getAllOrganizations(@Header("Authorization") accessToken : String) :
            Call<List<Organization>>

    @GET("organization/{orgId}")
    fun getOneOrganization(@Path("orgId") orgId : String, @Header("Authorization") accessToken : String) :
            Call<Organization>

    @GET("organization/members/{orgId}")
    fun getOrganizationMembers(@Path("orgId") orgId : String, @Header("Authorization") accessToken : String) :
            Call<List<OrganizationMember>>

    @POST("organization")
    fun createOrganization(@Body body : OrganizationCreateRequest, @Header("Authorization") accessToken : String) :
            Call<Organization>

    @PUT("organization")
    fun editOrganization(@Body body : OrganizationEditRequest, @Header("Authorization") accessToken : String) :
            Call<Organization>

    @DELETE("organization/{orgId}")
    fun deleteOrganization(@Path("orgId") orgId : String, @Header("Authorization") accessToken : String) :
            Call<String>

    @POST("organization/right/attach")
    fun attachRights(@Body body : AttachDetachRightRequest, @Header("Authorization") accessToken : String) :
            Call<Void>

    @POST("organization/right/detach")
    fun detachRights(@Body body : AttachDetachRightRequest, @Header("Authorization") accessToken : String) :
            Call<Void>



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