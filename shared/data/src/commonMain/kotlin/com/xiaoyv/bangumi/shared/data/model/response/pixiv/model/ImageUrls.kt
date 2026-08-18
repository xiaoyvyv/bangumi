package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param squareMedium
 * @param medium
 * @param large
 */
@Serializable

data class ImageUrls(

    @Contextual @SerialName(value = "square_medium")
    val squareMedium: String = "",

    @Contextual @SerialName(value = "medium")
    val medium: String = "",

    @Contextual @SerialName(value = "large")
    val large: String = ""
)

