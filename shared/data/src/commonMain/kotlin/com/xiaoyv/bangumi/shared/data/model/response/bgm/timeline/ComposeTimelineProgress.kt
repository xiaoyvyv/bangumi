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
import com.xiaoyv.bangumi.shared.core.types.TimelineProgressAction
import com.xiaoyv.bangumi.shared.core.utils.addUrl
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisodeRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineProgress(
    @SerialName("batch") val batch: ComposeTimelineBatch = ComposeTimelineBatch.Empty,
    @SerialName("single") val single: ComposeTimelineSingle = ComposeTimelineSingle.Empty,
) {
    @Composable
    fun rememberTimelineTitle(
        @TimelineProgressAction action: Int,
        highlightColor: Color = MaterialTheme.colorScheme.primary,
        onEpisodeClickListener: (ComposeEpisodeRelated) -> Unit = {},
        onSubjectClickListener: (ComposeSubject) -> Unit = {},
    ): AnnotatedString = remember(this, action, highlightColor, onEpisodeClickListener, onSubjectClickListener) {
        buildAnnotatedString {
            when (action) {
                TimelineProgressAction.WISH -> append("想看")
                TimelineProgressAction.DONE -> append("看过")
                TimelineProgressAction.DROPPED -> append("抛弃了")
                TimelineProgressAction.BATCH_DONE -> append("完成了")
            }
            append(" ")

            when {
                single != ComposeTimelineSingle.Empty -> {
                    addUrl(
                        text = "Ep.${single.episode.sort.toTrimString()} " + single.episode.displayName,
                        style = SpanStyle(highlightColor, textDecoration = TextDecoration.Underline),
                        listener = { onEpisodeClickListener(single.episode) }
                    )
                }

                batch != ComposeTimelineBatch.Empty -> {
                    addUrl(
                        text = batch.subject.displayName,
                        style = SpanStyle(highlightColor, textDecoration = TextDecoration.Underline),
                        listener = { onSubjectClickListener(batch.subject) }
                    )
                    if (batch.volsUpdate > 0) {
                        append(" ${batch.volsUpdate} of ${batch.volsTotal} 卷")
                    }
                    if (batch.epsUpdate > 0) {
                        append(" ${batch.epsUpdate} of ${batch.epsTotal} 话")
                    }
                }

                else -> append("更新了进度")
            }
        }
    }

    companion object {
        val Empty = ComposeTimelineProgress()
    }
}
