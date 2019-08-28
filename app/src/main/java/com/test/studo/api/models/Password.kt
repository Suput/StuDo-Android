package com.test.studo.api.models

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(

    @SerializedName("id")
    val id : String,

    @SerializedName("oldPassword")
    val oldPassword : String,

    @SerializedName("newPassword")
    val newPassword : String

)

data class ResetPasswordRequest(

    @SerializedName("email")
    val email : String

)