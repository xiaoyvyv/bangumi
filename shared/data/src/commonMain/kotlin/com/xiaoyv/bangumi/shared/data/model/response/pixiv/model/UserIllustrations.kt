package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param user
 * @param illusts
 * @param nextUrl
 */
@Serializable

data class UserIllustrations(

    @SerialName(value = "user")
    val user: UserInfo? = null,

    @SerialName(value = "illusts")
    val illusts: List<IllustrationInfo>? = null,

    @SerialName(value = "next_url")
    val nextUrl: String = ""

)

