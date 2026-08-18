package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param userPreviews
 * @param nextUrl
 */
@Serializable

data class UserFollowing(

    @SerialName(value = "user_previews")
    val userPreviews: List<UserPreview>? = null,

    @SerialName(value = "next_url")
    val nextUrl: String = ""

)

