package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param user
 * @param novels
 * @param nextUrl
 */
@Serializable

data class UserNovels(

    @SerialName(value = "user")
    val user: UserInfo? = null,

    @SerialName(value = "novels")
    val novels: List<NovelInfo>? = null,

    @SerialName(value = "next_url")
    val nextUrl: String = ""

)

