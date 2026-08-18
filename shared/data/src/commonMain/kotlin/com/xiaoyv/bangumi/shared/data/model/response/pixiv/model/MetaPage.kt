package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param imageUrls
 */
@Serializable

data class MetaPage(

    @SerialName(value = "image_urls")
    val imageUrls: ImageUrls? = null

)

