package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * [ComposeTopicDetail]
 *
 * @since 2025/5/11
 */
@Immutable
data class ComposeTopicDetail(
    val id: Long = 0,
    /**
     * 内容区域的ID，不是帖子ID，用于回复、贴贴等
     */
    val contentId: String = "",
    @field:TopicDetailType
    val type: String = TopicDetailType.TYPE_UNKNOWN,
    val title: String = "",
    val content: String = "",
    val contentHtml: AnnotatedString = AnnotatedString(""),
    val time: String = "",
    val commentCount: Int = 0,
    val user: ComposeUser = ComposeUser.Empty,
    val subjects: SerializeList<ComposeSubject> = persistentListOf(),
    val mono: ComposeMonoDisplay = ComposeMonoDisplay.Empty,
    val group: ComposeGroup = ComposeGroup.Empty,
    val emojiParam: ComposeEmojiParam = ComposeEmojiParam.Empty,
    val replyParam: ComposeCommentParam = ComposeCommentParam.Empty,
    val comments: SerializeList<ComposeComment> = persistentListOf(),
    val reactions: SerializeMap<String, SerializeList<ComposeReaction>> = persistentMapOf(),
) {
    val firstSubject get() = subjects.firstOrNull() ?: ComposeSubject.Empty

    val shareUrl: String
        get() = when (type) {
            TopicDetailType.TYPE_EP -> "https://bgm.tv/ep/topic/$id"
            TopicDetailType.TYPE_GROUP -> "https://bgm.tv/group/topic/$id"
            TopicDetailType.TYPE_PERSON -> "https://bgm.tv/person/$id"
            TopicDetailType.TYPE_CRT -> "https://bgm.tv/character/$id"
            TopicDetailType.TYPE_SUBJECT -> "https://bgm.tv/subject/$id"
            TopicDetailType.TYPE_INDEX -> "https://bgm.tv/index/$id"
            TopicDetailType.TYPE_BLOG -> "https://bgm.tv/blog/$id"
            else -> "https://bgm.tv"
        }

    companion object {
        val Empty = ComposeTopicDetail()
    }
}
