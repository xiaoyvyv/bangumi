package com.xiaoyv.bangumi.shared.ui.view.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTag
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf

@Composable
fun SubjectTagColumn(
    modifier: Modifier,
    subject: ComposeSubject,
    maxLines: Int = Int.MAX_VALUE,
    onClick: (ComposeTag) -> Unit = {},
) {
    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        maxLines = maxLines
    ) {
        subject.tags.fastForEach {
            SuggestionChip(
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
                            text = it.count.toString(),
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