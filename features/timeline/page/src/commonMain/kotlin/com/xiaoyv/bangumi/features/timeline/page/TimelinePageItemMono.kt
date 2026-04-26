package com.xiaoyv.bangumi.features.timeline.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf

@Composable
internal fun TimelinePageItemMono(
    item: ComposeTimeline,
    onClick: (ComposeMono, Int) -> Unit,
) {
    OutlinedCard {
        LazyRow(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        ) {
            if (item.memo.mono.persons.isNotEmpty()) {
                items(
                    items = item.memo.mono.persons,
                    contentType = { CONTENT_TYPE_TIMELINE_MONO }
                ) { mono ->
                    InfoImage(
                        modifier = Modifier.width(85.dp),
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
                        modifier = Modifier.width(85.dp),
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