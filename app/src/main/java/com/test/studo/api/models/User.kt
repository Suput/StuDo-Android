package com.test.studo.api.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(

	@SerializedName("id")
		val id: String,

	@SerializedName("firstname")
		val firstName: String,

	@SerializedName("surname")
		val secondName: String,

	@SerializedName("email")
		val email: String,

	@SerializedName("studentCardNumber")
		val studentCardNumber: String?
) : Serializable

data class UserLoginRequest (

	@SerializedName("Email")
	val email : String,

	@SerializedName("Password")
	val password : String

)

data class UserLoginResponse (

	@SerializedName("user")
	val user : User,

	@SerializedName("accessToken")
	val accessToken : String

)

data class UserRegistrationRequest(

	@SerializedName("firstname")
	val firstName : String,

	@SerializedName("surname")
	val secondName : String,

	@SerializedName("email")
	val email : String,

	@SerializedName("studentCardNumber")
	val studentCardNumber : String,

	@SerializedName("password")
	val password : String,

	@SerializedName("passwordConfirm")
	val passwordConfirm : String

)