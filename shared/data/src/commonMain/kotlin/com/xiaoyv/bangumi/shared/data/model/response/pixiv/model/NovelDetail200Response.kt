package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param novel
 */
@Serializable

data class NovelDetail200Response(

    @SerialName(value = "novel")
    val novel: NovelInfo? = null

)

