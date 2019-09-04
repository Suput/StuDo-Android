package com.test.studo

import com.test.studo.api.ApiService
import com.test.studo.api.models.CompactAd
import com.test.studo.api.models.CompactResume
import com.test.studo.api.models.Organization
import com.test.studo.api.models.UserLoginResponse
import java.text.SimpleDateFormat

val api = ApiService.create()

lateinit var currentUserWithToken : UserLoginResponse

var allAdsList : List<CompactAd>? = null

var allResumesList : List<CompactResume>? = null

var allOrganizationsList : List<Organization>? = null

val serverDataFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
val serverDataFormatWithoutMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
val clientDataFormat = SimpleDateFormat("dd.MM.yyyy")