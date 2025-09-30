package com.xiaoyv.bangumi.shared.data.model.response.bgm

import com.xiaoyv.bangumi.shared.core.types.RakuenFlagType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.types.ReportValueType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.ReportParam
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComposeTopic(
    @SerialName("updatedAt") val updatedAt: SerializeDateLong = 0,
    @SerialName("createdAt") val createdAt: SerializeDateLong = 0,
    @SerialName("creatorID") val creatorID: Long = 0,
    @SerialName("display") val display: Int = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("parentID") val parentID: Long = 0,
    @SerialName("replyCount") val replyCount: Int = 0,
    @SerialName("state") val state: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("group") val group: ComposeGroup = ComposeGroup.Empty,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("creator") val creator: ComposeUser = ComposeUser.Empty,
    @SerialName("mono") val mono: ComposeMonoDisplay = ComposeMonoDisplay.Empty,
    @SerialName("replies") val replies: SerializeList<ComposeCommentReply> = persistentListOf(),

    @field:RakuenIdType
    val topicType: String = RakuenIdType.TYPE_UNKNOWN,

    /**
     * 本地填充的 flags
     */
    @field:RakuenFlagType
    val flags: SerializeList<String> = persistentListOf(),
    /**
     * 搜索时填充项
     */
    val summary: String = "",
) {
    /**
     * 举报参数
     */
    fun reportParam(
        @ReportValueType value: Int, comment: String,
        formHash: String,
    ): ReportParam {
        return when (topicType) {
            RakuenIdType.TYPE_GROUP -> ReportParam(
                targetId = id,
                type = ReportType.GROUP_ARTICLE,
                value = value,
                comment = comment,
                formhash = formHash
            )

            RakuenIdType.TYPE_SUBJECT -> ReportParam(
                targetId = id,
                type = ReportType.SUBJECT_ARTICLE,
                value = value,
                comment = comment,
                formhash = formHash
            )

            RakuenIdType.TYPE_BLOG -> ReportParam(
                targetId = creator.id,
                type = ReportType.USER,
                value = value,
                comment = buildString {
                    append("https://bgm.tv/blog/$id")
                    appendLine()
                    append(comment)
                },
                formhash = formHash
            )

            else -> ReportParam.Empty
        }
    }

    fun opt(@RakuenIdType topicType: String): ComposeTopic {
        return copy(topicType = topicType)
    }

    companion object {
        val Empty = ComposeTopic()
    }
}
