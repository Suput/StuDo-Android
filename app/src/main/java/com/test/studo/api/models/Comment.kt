package com.test.studo.api.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CompactComment(

    @SerializedName("text")
    val text : String,

    @SerializedName("author")
    val author : String

)

data class Comment(

    @SerializedName("id")
    val id : String,

    @SerializedName("text")
    val text : String,

    @SerializedName("commentTime")
    val commentTime : String,

    @SerializedName("authorId")
    val authorId : String,

    @SerializedName("author")
    val author : String

) : Serializable

data class CreateCommentRequest(

    @SerializedName("text")
    val text: String

)