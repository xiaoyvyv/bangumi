package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class ComposeGroup(
    @SerialName("accessible") val accessible: Boolean = false,
    @SerialName("cat") val cat: Int = 0,
    @SerialName("createdAt") val createdAt: SerializeDateLong = 0,
    @SerialName("creator") val creator: ComposeUser = ComposeUser.Empty,
    @SerialName("creatorID") val creatorID: Long = 0,
    @SerialName("description") val description: String = "",
    @SerialName("icon") val images: ComposeImages = ComposeImages.Empty,
    @SerialName("id") val id: Long = 0,
    @SerialName("members") val members: Int = 0,
    @SerialName("membership") val membership: ComposeMembership = ComposeMembership.Empty,
    @SerialName("name") val name: String = "",
    @SerialName("nsfw") val nsfw: Boolean = false,
    @SerialName("posts") val posts: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("topics") val topics: Int = 0,
) {
    val shareUrl: String
        get() = WebConstant.URL_BASE_WEB + "group/" + name

    val isJoined get() = membership != ComposeMembership.Empty

    companion object {
        val Empty = ComposeGroup()
    }
}


