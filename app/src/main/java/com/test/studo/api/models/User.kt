package com.test.studo.api.models

import com.google.gson.annotations.SerializedName

data class User(

		@SerializedName("id")
		val id: String?,

		@SerializedName("firstname")
		val firstName: String?,

		@SerializedName("surname")
		val surname: String?,

		@SerializedName("email")
		val email: String?,

		@SerializedName("studentCardNumber")
		val studentCardNumber: String?
)
