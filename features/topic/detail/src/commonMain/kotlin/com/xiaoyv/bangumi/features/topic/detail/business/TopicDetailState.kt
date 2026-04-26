package com.xiaoyv.bangumi.features.topic.detail.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.parser.bbcode.parseAsBbcode
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
    @field:TopicDetailType
    val type: String = TopicDetailType.TYPE_UNKNOWN,
    val topic: ComposeTopic = ComposeTopic.Empty,
    val episode: ComposeEpisode = ComposeEpisode.Empty,
    val mono: ComposeMonoDisplay = ComposeMonoDisplay.Empty,
    val index: ComposeIndex = ComposeIndex.Empty,
    val blog: ComposeBlogEntry = ComposeBlogEntry.Empty,

    val selectedCommentSortFilter: Int = 0,
    val selectedCommentTypeFilter: Int = 0,
    val commentSortFilters: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    val commentTypeFilters: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    val comments: SerializeList<ComposeReply> = persistentListOf()
) {

    val displayTitle: String = when (type) {
        TopicDetailType.TYPE_EP -> "Ep.${episode.sortOrder} ${episode.displayName}"
        TopicDetailType.TYPE_GROUP -> topic.title
        TopicDetailType.TYPE_PERSON -> mono.mono.displayName
        TopicDetailType.TYPE_CRT -> mono.mono.displayName
        TopicDetailType.TYPE_SUBJECT -> topic.title
        TopicDetailType.TYPE_INDEX -> index.title
        TopicDetailType.TYPE_BLOG -> blog.title
        else -> ""
    }

    val displayReactions: ImmutableList<ComposeReaction> = when (type) {
        TopicDetailType.TYPE_SUBJECT -> topic.replies.firstOrNull()?.reactions.orEmpty().toImmutableList()
        TopicDetailType.TYPE_GROUP -> topic.replies.firstOrNull()?.reactions.orEmpty().toImmutableList()
        TopicDetailType.TYPE_BLOG -> blog.reactions
        else -> persistentListOf()
    }

    val displayContent: AnnotatedString = when (type) {
        TopicDetailType.TYPE_EP -> episode.description.parseAsBbcode()
        TopicDetailType.TYPE_SUBJECT,
        TopicDetailType.TYPE_GROUP -> topic.replies.firstOrNull()?.content.orEmpty().parseAsBbcode()

        TopicDetailType.TYPE_CRT,
        TopicDetailType.TYPE_PERSON -> mono.mono.summary.parseAsBbcode()

        TopicDetailType.TYPE_INDEX -> index.desc.parseAsBbcode()
        TopicDetailType.TYPE_BLOG -> blog.content.parseAsBbcode()
        else -> buildAnnotatedString { }
    }

    val shareUrl: String = when (type) {
        TopicDetailType.TYPE_EP -> "https://bgm.tv/ep/topic/$id"
        TopicDetailType.TYPE_GROUP -> "https://bgm.tv/group/topic/$id"
        TopicDetailType.TYPE_PERSON -> "https://bgm.tv/person/$id"
        TopicDetailType.TYPE_CRT -> "https://bgm.tv/character/$id"
        TopicDetailType.TYPE_SUBJECT -> "https://bgm.tv/subject/$id"
        TopicDetailType.TYPE_INDEX -> "https://bgm.tv/index/$id"
        TopicDetailType.TYPE_BLOG -> "https://bgm.tv/blog/$id"
        else -> "https://bgm.tv"
    }
}
