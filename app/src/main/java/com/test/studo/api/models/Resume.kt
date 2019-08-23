package com.test.studo.api.models

import com.google.gson.annotations.SerializedName

data class CompactResume(

    @SerializedName("id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("userName")
    val userName : String

)

data class Resume(

    @SerializedName("id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("userId")
    val userId : String,

    @SerializedName("user")
    val user : User

)

data class ResumeCreateRequest(

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String

)

data class ResumeEditRequest(

    @SerializedName("id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String
)