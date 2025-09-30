package com.xiaoyv.bangumi.shared.data.model.request


import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateContent(
    @SerialName(value = "content") @Required val content: String,
)

