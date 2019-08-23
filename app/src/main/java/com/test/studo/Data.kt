package com.test.studo

import com.test.studo.api.ApiService
import com.test.studo.api.models.CompactAd
import com.test.studo.api.models.CompactResume
import com.test.studo.api.models.UserLoginResponse

val api = ApiService.create()

lateinit var currentUserWithToken : UserLoginResponse

var compactAdList : List<CompactAd>? = null

var compactResumeList : List<CompactResume>? = null