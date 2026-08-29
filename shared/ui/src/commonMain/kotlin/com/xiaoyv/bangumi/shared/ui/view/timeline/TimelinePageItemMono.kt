package com.xiaoyv.bangumi.shared.ui.view.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.theme.ContentCoverWidth
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf

internal const val CONTENT_TYPE_TIMELINE_MONO = "CONTENT_TYPE_TIMELINE_MONO"

@Composable
internal fun TimelinePageItemMono(
    item: ComposeTimeline,
    onClick: (ComposeMono, Int) -> Unit,
) {
    OutlinedCard {
        LazyRow(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        ) {
            if (item.memo.mono.persons.isNotEmpty()) {
                items(
                    items = item.memo.mono.persons,
                    contentType = { CONTENT_TYPE_TIMELINE_MONO }
                ) { mono ->
                    InfoImage(
                        modifier = Modifier.width(ContentCoverWidth),
                        model = mono.images.displayMediumImage,
                        text = mono.displayName,
                        textPadding = 4.dp,
                        textMaxLines = 1,
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                        onClick = { onClick(mono, MonoType.PERSON) }
                    )
                }
            }
            if (item.memo.mono.characters.isNotEmpty()) {
                items(
                    items = item.memo.mono.characters,
                    contentType = { CONTENT_TYPE_TIMELINE_MONO }
                ) { mono ->
                    InfoImage(
                        modifier = Modifier.width(ContentCoverWidth),
                        model = mono.images.displayMediumImage,
                        text = mono.displayName,
                        textPadding = 4.dp,
                        textMaxLines = 1,
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                        onClick = { onClick(mono, MonoType.CHARACTER) }
                    )
                }
            }
        }
    }
}
