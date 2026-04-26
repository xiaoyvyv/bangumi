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
import com.xiaoyv.bangumi.shared.core.types.TimelineSubjectAction
import com.xiaoyv.bangumi.shared.core.utils.addUrl
import com.xiaoyv.bangumi.shared.core.utils.forEachWithSeparator
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineSubject(
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("comment") val comment: String = "",
    @SerialName("rate") val rate: Int = 0,
    @SerialName("collectID") val collectID: Long = 0,
)

@Composable
fun SerializeList<ComposeTimelineSubject>.rememberTimelineTitle(
    @TimelineSubjectAction action: Int,
    batch: Boolean,
    highlightColor: Color = MaterialTheme.colorScheme.primary,
    onSubjectClickListener: (ComposeSubject) -> Unit = {},
): AnnotatedString = remember(this, action, batch, highlightColor, onSubjectClickListener) {
    buildAnnotatedString {
        if (batch) {
            forEachWithSeparator(
                onStart = {
                    when (action) {
                        TimelineSubjectAction.WISH_READ -> append("想读")
                        TimelineSubjectAction.WISH_WATCH -> append("想看")
                        TimelineSubjectAction.WISH_LISTEN -> append("想听")
                        TimelineSubjectAction.WISH_PLAY -> append("想玩")
                        TimelineSubjectAction.DONE_READ -> append("读过")
                        TimelineSubjectAction.DONE_WATCH -> append("看过")
                        TimelineSubjectAction.DONE_LISTEN -> append("听过")
                        TimelineSubjectAction.DONE_PLAY -> append("玩过")
                        TimelineSubjectAction.DOING_READ -> append("在读")
                        TimelineSubjectAction.DOING_WATCH -> append("在看")
                        TimelineSubjectAction.DOING_LISTEN -> append("在听")
                        TimelineSubjectAction.DOING_PLAY -> append("在玩")
                        TimelineSubjectAction.ON_HOLD -> append("搁置了")
                        TimelineSubjectAction.DROPPED -> append("抛弃了")
                        else -> append("收藏了")
                    }
                    append(" ")
                },
                onItem = { subject ->
                    addUrl(
                        text = subject.subject.displayName,
                        style = SpanStyle(
                            color = highlightColor,
                            textDecoration = TextDecoration.Underline
                        ),
                        listener = { onSubjectClickListener(subject.subject) }
                    )
                },
                onSeparator = { append("、") },
                onEnd = {
                    append(" 等$size")

                    when (action) {
                        TimelineSubjectAction.WISH_READ -> append("本书")
                        TimelineSubjectAction.DONE_READ -> append("本书")
                        TimelineSubjectAction.DOING_READ -> append("本书")

                        TimelineSubjectAction.WISH_LISTEN -> append("张音乐")
                        TimelineSubjectAction.DONE_LISTEN -> append("张音乐")
                        TimelineSubjectAction.DOING_LISTEN -> append("张音乐")

                        TimelineSubjectAction.WISH_WATCH -> append("部番组")
                        TimelineSubjectAction.DOING_WATCH -> append("部番组")
                        TimelineSubjectAction.DONE_WATCH -> append("部番组")

                        TimelineSubjectAction.WISH_PLAY -> append("部游戏")
                        TimelineSubjectAction.DOING_PLAY -> append("部游戏")
                        TimelineSubjectAction.DONE_PLAY -> append("部游戏")

                        else -> append("个条目")
                    }
                }
            )
        } else {
            forEachWithSeparator(
                onStart = {
                    when (action) {
                        TimelineSubjectAction.WISH_READ -> append("想读")
                        TimelineSubjectAction.WISH_WATCH -> append("想看")
                        TimelineSubjectAction.WISH_LISTEN -> append("想听")
                        TimelineSubjectAction.WISH_PLAY -> append("想玩")
                        TimelineSubjectAction.DONE_READ -> append("读过")
                        TimelineSubjectAction.DONE_WATCH -> append("看过")
                        TimelineSubjectAction.DONE_LISTEN -> append("听过")
                        TimelineSubjectAction.DONE_PLAY -> append("玩过")
                        TimelineSubjectAction.DOING_READ -> append("在读")
                        TimelineSubjectAction.DOING_WATCH -> append("在看")
                        TimelineSubjectAction.DOING_LISTEN -> append("在听")
                        TimelineSubjectAction.DOING_PLAY -> append("在玩")
                        TimelineSubjectAction.ON_HOLD -> append("搁置了")
                        TimelineSubjectAction.DROPPED -> append("抛弃了")
                        else -> append("收藏了")
                    }
                    append(" ")
                },
                onItem = { subject ->
                    addUrl(
                        text = subject.subject.displayName,
                        style = SpanStyle(
                            color = highlightColor,
                            textDecoration = TextDecoration.Underline
                        ),
                        listener = { onSubjectClickListener(subject.subject) }
                    )
                },
                onSeparator = { append("、") }
            )
        }
    }
}