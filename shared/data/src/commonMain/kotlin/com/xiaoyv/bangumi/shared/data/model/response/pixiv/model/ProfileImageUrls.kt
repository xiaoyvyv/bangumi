package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param medium
 */
@Serializable

data class ProfileImageUrls(

    @Contextual @SerialName(value = "medium")
    val medium: String = ""

)

