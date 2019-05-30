package com.test.studo.api.models

import com.google.gson.annotations.SerializedName

data class UserRequest (

    @SerializedName("Email")
    val email : String,

    @SerializedName("Password")
    val password : String

)