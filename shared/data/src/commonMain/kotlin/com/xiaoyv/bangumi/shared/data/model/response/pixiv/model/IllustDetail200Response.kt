package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param illust
 */
@Serializable

data class IllustDetail200Response(

    @SerialName(value = "illust")
    val illust: IllustrationInfo? = null

)

