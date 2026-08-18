package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param id
 * @param comment
 * @param date
 * @param user
 */
@Serializable

data class Comment(

    @SerialName(value = "id")
    val id: Long = 0,

    @SerialName(value = "comment")
    val comment: String = "",

    @SerialName(value = "date")
    val date: String = "",

    @SerialName(value = "user")
    val user: CommentUser? = null

)

