package com.xiaoyv.bangumi.shared.ui.view.subject

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.formatShort
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTag
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf

@Composable
fun SubjectTagRow(
    modifier: Modifier,
    tags: SerializeList<ComposeTag>,
    @IntRange(from = 1) maxLines: Int = 3,
    onClick: (ComposeTag) -> Unit = {},
) {
    val density = LocalDensity.current
    var itemHeight by rememberSaveable { mutableStateOf(32f) }

    LazyHorizontalStaggeredGrid(
        modifier = modifier.height(itemHeight.dp * maxLines + LayoutPaddingHalf * (maxLines - 1)),
        rows = StaggeredGridCells.Fixed(maxLines),
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        horizontalItemSpacing = LayoutPaddingHalf
    ) {
        items(tags) {
            SuggestionChip(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    itemHeight = with(density) { coordinates.size.height.toDp().value }
                },
                border = null,
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                label = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        if (it.count > 0) Text(
                            text = it.count.formatShort(1),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                onClick = { onClick(it) }
            )
        }
    }
}