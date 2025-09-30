package com.xiaoyv.bangumi.shared.data.model.response.bgm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ComposeUserDisplay(
    @SerialName("joinedAt") val joinedAt: Long = 0,
    @SerialName("role") val role: Int = 0,
    @SerialName("uid") val uid: Long = 0,
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,
    @SerialName("grade") val grade: Int = 0,
    @SerialName("description") val description: String = "",
    @SerialName("createdAt") val createdAt: Long = 0,
    @SerialName("pinyin") val pinyin: String = "",
)