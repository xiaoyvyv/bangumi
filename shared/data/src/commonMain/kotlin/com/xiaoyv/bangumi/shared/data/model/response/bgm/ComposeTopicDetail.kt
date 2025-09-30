package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
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
    @field:RakuenIdType
    val type: String = RakuenIdType.TYPE_UNKNOWN,
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
            RakuenIdType.TYPE_EP -> "https://bgm.tv/ep/topic/$id"
            RakuenIdType.TYPE_GROUP -> "https://bgm.tv/group/topic/$id"
            RakuenIdType.TYPE_PERSON -> "https://bgm.tv/person/$id"
            RakuenIdType.TYPE_CRT -> "https://bgm.tv/character/$id"
            RakuenIdType.TYPE_SUBJECT -> "https://bgm.tv/subject/$id"
            RakuenIdType.TYPE_INDEX -> "https://bgm.tv/index/$id"
            RakuenIdType.TYPE_BLOG -> "https://bgm.tv/blog/$id"
            else -> "https://bgm.tv"
        }

    companion object {
        val Empty = ComposeTopicDetail()
    }
}
