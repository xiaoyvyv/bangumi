package com.xiaoyv.bangumi.features.topic.detail.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * [TopicDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TopicDetailState(
    val id: Long = 0,
    @field:TopicType
    val type: String = TopicType.TYPE_UNKNOWN,
    val topic: ComposeTopic = ComposeTopic.Empty,
    val episode: ComposeEpisode = ComposeEpisode.Empty,
    val mono: ComposeMonoDisplay = ComposeMonoDisplay.Empty,
    val index: ComposeIndex = ComposeIndex.Empty,
    val blog: ComposeBlogEntry = ComposeBlogEntry.Empty,

    val selectedCommentSortFilter: Int = 0,
    val selectedCommentTypeFilter: Int = 0,
    val commentSortFilters: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    val commentTypeFilters: SerializeList<ComposeTextTab<Int>> = persistentListOf(),

    /**
     * 小组话题或条目话题的回复在附加在详情的数据内，其它类型的都是分页加载的评论了
     */
    val replies: SerializeList<ComposeReply> = persistentListOf()
) {

    val displayTitle: String = when (type) {
        TopicType.TYPE_EP -> "Ep.${episode.sortOrder} ${episode.displayName}"
        TopicType.TYPE_GROUP -> topic.title
        TopicType.TYPE_PERSON -> mono.mono.displayName
        TopicType.TYPE_CRT -> mono.mono.displayName
        TopicType.TYPE_SUBJECT -> topic.title
        TopicType.TYPE_INDEX -> index.title
        TopicType.TYPE_BLOG -> blog.title
        else -> ""
    }

    /**
     * 话题的贴贴
     */
    val displayReactions: ImmutableList<ComposeReaction> = when (type) {
        TopicType.TYPE_SUBJECT -> topic.replies.firstOrNull()?.reactions.orEmpty().toImmutableList()
        TopicType.TYPE_GROUP -> topic.replies.firstOrNull()?.reactions.orEmpty().toImmutableList()
        TopicType.TYPE_BLOG -> blog.reactions
        else -> persistentListOf()
    }

    /**
     * 话题的主题内容区域
     */
    val displayContentText: String = when (type) {
        TopicType.TYPE_SUBJECT,
        TopicType.TYPE_GROUP -> topic.replies.firstOrNull()?.content.orEmpty()

        TopicType.TYPE_EP -> episode.description

        TopicType.TYPE_CRT,
        TopicType.TYPE_PERSON -> mono.mono.summary

        TopicType.TYPE_INDEX -> index.desc
        TopicType.TYPE_BLOG -> blog.content
        else -> ""
    }

    val shareUrl: String = when (type) {
        TopicType.TYPE_EP -> "https://bgm.tv/ep/topic/$id"
        TopicType.TYPE_GROUP -> "https://bgm.tv/group/topic/$id"
        TopicType.TYPE_PERSON -> "https://bgm.tv/person/$id"
        TopicType.TYPE_CRT -> "https://bgm.tv/character/$id"
        TopicType.TYPE_SUBJECT -> "https://bgm.tv/subject/$id"
        TopicType.TYPE_INDEX -> "https://bgm.tv/index/$id"
        TopicType.TYPE_BLOG -> "https://bgm.tv/blog/$id"
        else -> "https://bgm.tv"
    }
}
