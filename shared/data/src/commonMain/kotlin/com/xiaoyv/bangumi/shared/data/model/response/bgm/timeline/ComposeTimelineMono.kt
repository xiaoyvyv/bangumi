package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.TimelineMonoType
import com.xiaoyv.bangumi.shared.core.utils.addUrl
import com.xiaoyv.bangumi.shared.core.utils.forEachWithSeparator
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineMono(
    @SerialName("characters") val characters: SerializeList<ComposeMono> = persistentListOf(),
    @SerialName("persons") val persons: SerializeList<ComposeMono> = persistentListOf(),
) {
    @Composable
    fun rememberTimelineTitle(
        highlightColor: Color = MaterialTheme.colorScheme.primary,
        onMonoClickListener: (ComposeMono, Int) -> Unit = { _, _ -> },
    ): AnnotatedString = remember(this, highlightColor, onMonoClickListener) {
        buildAnnotatedString {
            when {
                characters.isNotEmpty() -> characters.forEachWithSeparator(
                    onStart = { append("收藏了角色 ") },
                    onItem = { mono ->
                        addUrl(
                            text = mono.displayName,
                            style = SpanStyle(color = highlightColor, textDecoration = TextDecoration.Underline),
                            listener = { onMonoClickListener(mono, MonoType.CHARACTER) }
                        )
                    },
                    onSeparator = { append("、") }
                )

                persons.isNotEmpty() -> persons.forEachWithSeparator(
                    onStart = { append("收藏了人物 ") },
                    onItem = { mono ->
                        addUrl(
                            text = mono.displayName,
                            style = SpanStyle(color = highlightColor, textDecoration = TextDecoration.Underline),
                            listener = { onMonoClickListener(mono, MonoType.PERSON) }
                        )
                    },
                    onSeparator = { append("、") }
                )
            }
        }
    }

    companion object {
        val Empty = ComposeTimelineMono()
    }
}