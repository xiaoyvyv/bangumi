package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param user
 * @param profile
 * @param profilePublicity
 * @param workspace
 */
@Serializable

data class UserInfoDetailed(

    @SerialName(value = "user")
    val user: UserInfo? = null,

    @SerialName(value = "profile")
    val profile: Profile? = null,

    @SerialName(value = "profile_publicity")
    val profilePublicity: ProfilePublicity? = null,

    @SerialName(value = "workspace")
    val workspace: Workspace? = null

)

