package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.data.constant.userImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeSearchTopic(
    @SerialName("bgmGrp") val bgmGrp: String = "",
    @SerialName("bgmGrpAvatar") val bgmGrpAvatar: String = "",
    @SerialName("bgmGrpName") val bgmGrpName: String = "",
    @SerialName("id") val id: Long = 0,
    @SerialName("image") val image: String = "",
    @SerialName("summary") val summary: String = "",
    @SerialName("time") val time: String = "",
    @SerialName("timeDate") val timeDate: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("uid") val uid: String = "",
    @SerialName("userAvatar") val userAvatar: String = "",
    @SerialName("userName") val userName: String = "",
) {
    /**
     * 搜索的帖子全部是小组帖
     */
    fun toComposeTopic(): ComposeTopic {
        return ComposeTopic(
            id = id,
            title = title,
            summary = summary,
            topicType = RakuenIdType.TYPE_GROUP,
            createdAt = timeDate,
            updatedAt = timeDate,
            group = ComposeGroup(
                name = bgmGrp,
                title = bgmGrpName,
                images = ComposeImages.fromUrl(bgmGrpAvatar),
            ),
            creator = ComposeUser(
                username = uid,
                nickname = userName,
                avatar = ComposeImages.fromUrl(userAvatar.ifBlank { userImage(userName) }),
            )
        )
    }
}