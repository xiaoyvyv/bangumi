@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.data.model.response.bgm.rakuen

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.RakuenFlagType
import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@Immutable
data class ComposeRakuenTopic(
    @SerialName("type") @RakuenType val type: String = RakuenType.ALL,
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("replyCount") @JsonNames("comment") val replyCount: Int = 0,
    @SerialName("creator") val creator: ComposeUser = ComposeUser.Empty,
    @SerialName("group") val group: ComposeGroup = ComposeGroup.Empty,
    @SerialName("updatedAt") val updatedAt: SerializeDateLong = 0,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("episode") val episode: ComposeEpisode = ComposeEpisode.Empty,
    @SerialName("name") val name: String = "",
    @SerialName("nameCN") @JsonNames("name_cn", "nameCn") val nameCN: String = "",
    @SerialName("images") val images: ComposeImages = ComposeImages.Empty,

    /**
     * 本地填充的 flags
     */
    @field:RakuenFlagType
    val flags: SerializeList<String> = persistentListOf(),
) {
    val key get() = "$type-$id"

    val displayName: String get() = nameCN.ifBlank { name }

    val shareUrl: String
        get() = when (type) {
            RakuenType.GROUP -> WebConstant.URL_BASE_WEB + "group/topic/$id"
            RakuenType.MY_GROUP -> WebConstant.URL_BASE_WEB + "group/topic/$id"
            RakuenType.SUBJECT -> WebConstant.URL_BASE_WEB + "subject/topic/$id"
            RakuenType.EP -> WebConstant.URL_BASE_WEB + "ep/$id"
            RakuenType.CHARACTER -> WebConstant.URL_BASE_WEB + "character/$id"
            RakuenType.PERSON -> WebConstant.URL_BASE_WEB + "person/$id"
            else -> TopicType.TYPE_UNKNOWN
        }

    /**
     * 对应的话题类型
     */
    val topicType: String
        get() = when (type) {
            RakuenType.GROUP -> TopicType.TYPE_GROUP
            RakuenType.MY_GROUP -> TopicType.TYPE_GROUP
            RakuenType.SUBJECT -> TopicType.TYPE_SUBJECT
            RakuenType.EP -> TopicType.TYPE_EP
            RakuenType.CHARACTER -> TopicType.TYPE_CRT
            RakuenType.PERSON -> TopicType.TYPE_PERSON
            else -> TopicType.TYPE_UNKNOWN
        }

    /**
     * 举报类型
     */
    val reportType: Int
        get() = when (type) {
            RakuenType.GROUP -> ReportType.GROUP_ARTICLE
            RakuenType.MY_GROUP -> ReportType.GROUP_ARTICLE
            RakuenType.SUBJECT -> ReportType.SUBJECT_ARTICLE
            else -> ReportType.UNKNOWN
        }

    fun toMono(): ComposeMonoDisplay {
        val monoType = if (type == RakuenType.CHARACTER) MonoType.CHARACTER else MonoType.PERSON
        return ComposeMonoDisplay(
            type = monoType,
            info = ComposeMonoInfo(
                mono = ComposeMono(
                    type = monoType,
                    id = id,
                    nameCN = nameCN,
                    name = name,
                    updatedAt = updatedAt,
                    comment = replyCount,
                    images = images
                )
            )
        )
    }

    companion object {
        val Empty = ComposeRakuenTopic()
    }
}