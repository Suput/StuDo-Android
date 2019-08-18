package com.test.studo.api.models

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(

    @SerializedName("email")
    val email : String,

    @SerializedName("oldPassword")
    val oldPassword : String,

    @SerializedName("newPassword")
    val newPassword : String

)