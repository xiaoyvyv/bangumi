@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.types.UserGroup
import com.xiaoyv.bangumi.shared.core.utils.optImageUrl
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.native.SqlUser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlin.jvm.JvmStatic

@Immutable
@Serializable
data class ComposeUser(
    @SerialName("id") val id: Long = 0,
    @SerialName("avatar") val avatar: ComposeImages = ComposeImages.Empty,
    @SerialName("username") val username: String = "",
    @SerialName("nickname") val nickname: String = "",
    @SerialName("sign") val sign: String = "",
    @SerialName("group") @JsonNames("user_group", "group") @field:UserGroup.Group val group: Long = UserGroup.USER,
    @SerialName("bio") val bio: String = "",
    @SerialName("homepage") val homepage: ComposeUserHomepage = ComposeUserHomepage.Empty,
    @SerialName("joinedAt") val joinedAt: SerializeDateLong = 0,
    @SerialName("location") val location: String = "",
    @SerialName("networkServices") val networkServices: SerializeList<ComposeUserNetworkService> = persistentListOf(),
    @SerialName("site") val site: String = "",
    @SerialName("stats") val stats: ComposeUserStats = ComposeUserStats.Empty,

    /**
     * 其它非 API 扩展信息
     */
    @SerialName("roomPic") val roomPic: String = "",
    @SerialName("formHash") val formHash: String = "",
    @SerialName("updateAt") val updateAt: SerializeDateLong = System.currentTimeMillis(),
    @SerialName("online") val online: String = "",
) {
    val shareUrl: String get() = WebConstant.URL_BASE_WEB + "user/" + username

    val displayAvatar: String
        get() = avatar.large
            .ifBlank { avatar.medium }
            .ifBlank { avatar.small }


    fun opt(): ComposeUser {
        return copy(
            avatar = avatar.copy(
                medium = avatar.medium.optImageUrl(),
                large = avatar.large.optImageUrl()
            )
        )
    }

    companion object {
        val Empty: ComposeUser = ComposeUser()

        @JvmStatic
        fun from(user: SqlUser?): ComposeUser {
            return if (user == null) Empty else ComposeUser(
                id = user.id,
                avatar = ComposeImages.fromUrl(user.avatar),
                username = user.username,
                nickname = user.nickname,
                sign = user.sign,
                roomPic = user.roomPic,
                bio = user.summary,
                formHash = user.formHash,
                updateAt = user.updateAt,
                online = user.online,
                group = user.userGroup
            )
        }
    }
}

