package com.test.studo.api.models

import com.google.gson.annotations.SerializedName

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

)