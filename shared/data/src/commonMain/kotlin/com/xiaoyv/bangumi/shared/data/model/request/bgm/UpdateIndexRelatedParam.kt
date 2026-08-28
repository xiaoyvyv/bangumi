package com.xiaoyv.bangumi.shared.data.model.request.bgm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateIndexRelatedParam(
    @SerialName(value = "order")
    val order: Int,
    @SerialName(value = "comment")
    val comment: String
)