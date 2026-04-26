package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.core.types.TimelineCat.Companion.BLOG
import com.xiaoyv.bangumi.shared.core.types.TimelineCat.Companion.DAILY
import com.xiaoyv.bangumi.shared.core.types.TimelineCat.Companion.INDEX
import com.xiaoyv.bangumi.shared.core.types.TimelineCat.Companion.MONO
import com.xiaoyv.bangumi.shared.core.types.TimelineCat.Companion.PROGRESS
import com.xiaoyv.bangumi.shared.core.types.TimelineCat.Companion.STATUS
import com.xiaoyv.bangumi.shared.core.types.TimelineCat.Companion.SUBJECT
import com.xiaoyv.bangumi.shared.core.types.TimelineCat.Companion.WIKI
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisodeRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimeline(
    @SerialName("batch") val batch: Boolean = false,
    @SerialName("cat") @field:TimelineCat val cat: Int = TimelineCat.UNKNOWN,
    @SerialName("createdAt") val createdAt: SerializeDateLong = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("memo") val memo: ComposeTimelineMemo = ComposeTimelineMemo.Empty,
    @SerialName("replies") val replies: Int = 0,
    @SerialName("source") val source: ComposeTimelineSource = ComposeTimelineSource.Empty,
    @SerialName("type") val type: Int = 0,
    @SerialName("uid") val uid: Long = 0,
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,
) {
    @Composable
    fun rememberTimelineTitle(
        onUserClickListener: (ComposeUser) -> Unit = { },
        onGroupClickListener: (ComposeGroup) -> Unit = { },
        onSubjectClickListener: (ComposeSubject) -> Unit = { },
        onEpisodeClickListener: (ComposeEpisodeRelated) -> Unit = {},
        onBlogClickListener: (ComposeBlogEntry) -> Unit = { },
        onIndexClickListener: (ComposeIndex) -> Unit = {},
        onMonoClickListener: (ComposeMono, Int) -> Unit = { _, _ -> },
    ): AnnotatedString = when (cat) {
        DAILY -> memo.daily.rememberTimelineTitle(
            action = type,
            onUserClickListener = onUserClickListener,
            onGroupClickListener = onGroupClickListener
        )

        WIKI -> memo.wiki.rememberTimelineTitle(
            action = type,
            onSubjectClickListener = onSubjectClickListener
        )

        SUBJECT -> memo.subject.rememberTimelineTitle(
            action = type,
            batch = batch,
            onSubjectClickListener = onSubjectClickListener
        )

        PROGRESS -> memo.progress.rememberTimelineTitle(
            action = type,
            onSubjectClickListener = onSubjectClickListener,
            onEpisodeClickListener = onEpisodeClickListener
        )

        STATUS -> memo.status.rememberTimelineTitle(action = type)
        BLOG -> memo.blog.rememberTimelineTitle(
            onBlogClickListener = onBlogClickListener
        )

        INDEX -> memo.index.rememberTimelineTitle(
            onIndexClickListener = onIndexClickListener
        )

        MONO -> memo.mono.rememberTimelineTitle(
            onMonoClickListener = onMonoClickListener,
        )

        else -> AnnotatedString("")
    }
}

