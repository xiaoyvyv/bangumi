package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param originalImageUrl
 */
@Serializable

data class MetaSinglePage(

    @Contextual @SerialName(value = "original_image_url")
    val originalImageUrl: String = ""

)

