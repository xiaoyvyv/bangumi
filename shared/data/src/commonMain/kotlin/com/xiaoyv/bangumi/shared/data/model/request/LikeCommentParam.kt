package com.xiaoyv.bangumi.shared.data.model.request


import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param `value`
 */
@Serializable
data class LikeCommentParam(
    @SerialName(value = "value") @Required val `value`: Int,
)
