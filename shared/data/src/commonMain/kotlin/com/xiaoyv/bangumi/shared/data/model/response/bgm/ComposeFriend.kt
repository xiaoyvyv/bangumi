package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeFriend(
    @SerialName("index") val index: String = "",
    @SerialName("nickname") val nickname: String = "",
    @SerialName("uid") val uid: String = "",
    @SerialName("username") val username: String = "",
    @SerialName("avatar") val avatar: ComposeImages = ComposeImages.Empty,
    @SerialName("pinyin") val pinyin: String = "",
) {
    val displayAvatar: String
        get() = avatar.medium
            .ifBlank { avatar.large }
            .ifBlank { avatar.small }

    companion object {
        val Empty = ComposeFriend()
    }
}