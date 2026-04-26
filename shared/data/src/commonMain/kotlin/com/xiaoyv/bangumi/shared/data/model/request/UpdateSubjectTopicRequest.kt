package com.xiaoyv.bangumi.shared.data.model.request


import kotlinx.serialization.*

/**
 * @param content bbcode
 * @param title 
 */
@Serializable
data class UpdateSubjectTopicRequest (
    /* bbcode */
    @SerialName(value = "content") @Required val content: String,
    @SerialName(value = "title") @Required val title: String
)

