package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param id
 * @param name
 * @param account
 * @param profileImageUrls
 * @param comment
 * @param isFollowed
 * @param isAccessBlockingUser
 * @param isAcceptRequest
 */
@Serializable

data class UserInfo(

    @SerialName(value = "id")
    val id: Long = 0,

    @SerialName(value = "name")
    val name: String = "",

    @SerialName(value = "account")
    val account: String = "",

    @SerialName(value = "profile_image_urls")
    val profileImageUrls: ProfileImageUrls? = null,

    @SerialName(value = "comment")
    val comment: String = "",

    @SerialName(value = "is_followed")
    val isFollowed: Boolean? = null,

    @SerialName(value = "is_access_blocking_user")
    val isAccessBlockingUser: Boolean? = null,

    @SerialName(value = "is_accept_request")
    val isAcceptRequest: Boolean? = null

)

