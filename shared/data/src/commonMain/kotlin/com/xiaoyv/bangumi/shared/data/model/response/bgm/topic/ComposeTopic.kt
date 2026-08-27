package com.xiaoyv.bangumi.shared.data.model.response.bgm.topic

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.RakuenFlagType
import com.xiaoyv.bangumi.shared.core.types.ReportReason
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.manager.bbcodeToHtml
import com.xiaoyv.bangumi.shared.data.model.request.bgm.ReportParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.normalizedReplies
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
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
    @SerialName("replies") val replies: SerializeList<ComposeReply> = persistentListOf(),

    @field:TopicType
    val topicType: String = TopicType.TYPE_UNKNOWN,

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
    val contentPostId get() = replies.firstOrNull()?.id ?: 0

    val shareUrl: String get() = WebConstant.URL_BASE_WEB

    /**
     * 举报参数
     */
    fun reportParam(
        @ReportReason value: Int, comment: String,
        formHash: String,
    ): ReportParam {
        return when (topicType) {
            TopicType.TYPE_GROUP -> ReportParam(
                targetId = id,
                type = ReportType.GROUP_ARTICLE,
                value = value,
                comment = comment,
                formhash = formHash
            )

            TopicType.TYPE_SUBJECT -> ReportParam(
                targetId = id,
                type = ReportType.SUBJECT_ARTICLE,
                value = value,
                comment = comment,
                formhash = formHash
            )

            TopicType.TYPE_BLOG -> ReportParam(
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

    fun normalized(@TopicType topicType: String): ComposeTopic {
        return copy(
            topicType = topicType,
            summary = summary.bbcodeToHtml(),
            replies = replies.normalizedReplies()
        )
    }

    companion object {
        val Empty = ComposeTopic()
    }
}
