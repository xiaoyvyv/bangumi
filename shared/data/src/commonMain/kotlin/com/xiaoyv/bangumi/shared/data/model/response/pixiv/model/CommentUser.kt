package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param id
 * @param name
 * @param account
 * @param profileImageUrls
 */
@Serializable

data class CommentUser(

    @SerialName(value = "id")
    val id: Long = 0,

    @SerialName(value = "name")
    val name: String = "",

    @SerialName(value = "account")
    val account: String = "",

    @SerialName(value = "profile_image_urls")
    val profileImageUrls: ProfileImageUrls? = null

)

