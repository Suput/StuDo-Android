package com.test.studo.api.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CompactAd(

    @SerializedName("id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("shortDescription")
    val shortDescription : String,

    @SerializedName("beginTime")
    val beginTime : String,

    @SerializedName("endTime")
    val endTime : String,

    @SerializedName("userName")
    val userName : String,

    @SerializedName("organizationName")
    val organizationName : String?

)

data class Ad(

    @SerializedName("id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("shortDescription")
    val shortDescription : String,

    @SerializedName("beginTime")
    val beginTime : String,

    @SerializedName("endTime")
    val endTime : String,

    @SerializedName("userId")
    val userId : String,

    @SerializedName("user")
    val user : User,

    @SerializedName("organizationId")
    val organizationId : String?,

    @SerializedName("organization")
    val organization : Organization?

) : Serializable

data class AdCreateRequest(

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("shortDescription")
    val shortDescription : String,

    @SerializedName("beginTime")
    val beginTime : String,

    @SerializedName("endTime")
    val endTime : String,

    @SerializedName("organizationId")
    val organizationId : String?

)

data class AdEditRequest(

    @SerializedName("id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("shortDescription")
    val shortDescription : String,

    @SerializedName("beginTime")
    val beginTime : String,

    @SerializedName("endTime")
    val endTime : String

)