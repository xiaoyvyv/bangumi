package com.xiaoyv.bangumi.shared.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param title 日志标题
 * @param content 日志正文（BBCode）
 * @param turnstileToken 需要 [turnstile]
 * @param tags
 * @param public 公开（true）或仅好友可见（false），默认公开
 * @param subjectIDs
 */
@Serializable
data class CreateBlogEntryRequest(
    @SerialName("title")
    val title: String,

    @SerialName("content")
    val content: String,

    @SerialName("turnstileToken")
    val turnstileToken: String,

    @SerialName("tags")
    val tags: List<String>? = null,

    @SerialName("public")
    val public: Boolean? = null,

    @SerialName("subjectIDs")
    val subjectIDs: List<Int>? = null
)
