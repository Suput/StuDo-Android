package com.test.studo.api.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Organization(

    @SerializedName("id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String,

    @SerializedName("creatorId")
    val creatorId : String,

    @SerializedName("creator")
    val creator : User

) : Serializable

data class OrganizationEditRequest(

    @SerializedName("id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String

)

data class OrganizationCreateRequest(

    @SerializedName("name")
    val name : String,

    @SerializedName("description")
    val description : String

)

data class AttachDetachRightRequest(

    @SerializedName("organizationId")
    val organizationId : String,

    @SerializedName("userId")
    val userId : String,

    @SerializedName("right")
    val right : String

)

data class OrganizationMember(

    @SerializedName("user")
    val user : User,

    @SerializedName("organizationRights")
    val organizationRights : String

)