package com.test.studo.api.models

import com.google.gson.annotations.SerializedName

data class UserResponse (

    @SerializedName("user")
    val user : User?,

    @SerializedName("accessToken")
    val accessToken : String?

)

var userResponse : UserResponse? = null