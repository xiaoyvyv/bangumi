package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.xiaoyv.bangumi.shared.core.types.TimelineWikiAction
import com.xiaoyv.bangumi.shared.core.utils.addUrl
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineWiki(
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
) {
    @Composable
    fun rememberTimelineTitle(
        @TimelineWikiAction action: Int,
        highlightColor: Color = MaterialTheme.colorScheme.primary,
        onSubjectClickListener: (ComposeSubject) -> Unit = { },
    ): AnnotatedString = remember(this, action, highlightColor, onSubjectClickListener) {
        buildAnnotatedString {
            when (action) {
                TimelineWikiAction.ADD_BOOK -> append("添加了新书")
                TimelineWikiAction.ADD_ANIME -> append("添加了新动画")
                TimelineWikiAction.ADD_MUSIC -> append("添加了新唱片")
                TimelineWikiAction.ADD_GAME -> append("添加了新游戏")
                TimelineWikiAction.ADD_BOOK_SERIES -> append("添加了新图书系列")
                TimelineWikiAction.ADD_VIDEO -> append("添加了新影视")
                else -> append("WIKI操作")
            }
            append(" ")
            addUrl(
                text = subject.displayName,
                style = SpanStyle(
                    color = highlightColor,
                    textDecoration = TextDecoration.Underline
                ),
                listener = { onSubjectClickListener(subject) }
            )
        }
    }

    companion object {
        val Empty = ComposeTimelineWiki()
    }
}