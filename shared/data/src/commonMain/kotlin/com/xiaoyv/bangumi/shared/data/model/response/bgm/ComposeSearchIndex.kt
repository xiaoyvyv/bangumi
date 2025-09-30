package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.constant.userImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeSearchIndex(
    @SerialName("ban") val ban: Int = 0,
    @SerialName("collects") val collects: Int = 0,
    @SerialName("comments") val comments: Int = 0,
    @SerialName("createdAt") val createdAt: Long = 0,
    @SerialName("description") val description: String = "",
    @SerialName("id") val id: Long = 0,
    @SerialName("nickname") val nickname: String = "",
    @SerialName("nsfw") val nsfw: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("total") val total: Int = 0,
    @SerialName("updatedAt") val updatedAt: Long = 0,
    @SerialName("username") val username: String = "",
) {
    /**
     * 搜索的目录转为站内目录
     */
    fun toComposeIndex(): ComposeIndex {
        return ComposeIndex(
            id = id,
            title = title,
            desc = description,
            createdAt = createdAt,
            updatedAt = updatedAt,
            collects = collects,
            replies = comments,
            total = total,
            nsfw = nsfw == 1,
            creator = ComposeUser(
                username = username,
                nickname = nickname,
                avatar = ComposeImages.fromUrl(userImage(username))
            )
        )
    }
}