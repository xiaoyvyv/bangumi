package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param totalComments
 * @param comments
 * @param nextUrl
 * @param commentAccessControl
 */
@Serializable

data class NovelComments(

    @SerialName(value = "total_comments")
    val totalComments: Int = 0,

    @SerialName(value = "comments")
    val comments: List<Comment>? = null,

    @SerialName(value = "next_url")
    val nextUrl: String = "",

    @SerialName(value = "comment_access_control")
    val commentAccessControl: Int = 0

)

