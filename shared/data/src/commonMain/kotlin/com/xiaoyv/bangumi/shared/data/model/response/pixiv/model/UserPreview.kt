package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param user
 * @param illusts
 * @param novels
 * @param isMuted
 */
@Serializable

data class UserPreview(

    @SerialName(value = "user")
    val user: UserInfo? = null,

    @SerialName(value = "illusts")
    val illusts: List<IllustrationInfo>? = null,

    @SerialName(value = "novels")
    val novels: List<NovelInfo>? = null,

    @SerialName(value = "is_muted")
    val isMuted: Boolean? = null

)

