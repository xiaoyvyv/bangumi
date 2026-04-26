package com.xiaoyv.bangumi.features.timeline.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.ui.component.bar.RatingBar
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor

@Composable
internal fun TimelinePageItemComment(
    modifier: Modifier,
    comment: String,
    rate: Double = .0,
    contentPaddings: PaddingValues = PaddingValues(12.dp),
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPaddings),
            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
        ) {
            if (rate > 0) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    RatingBar(
                        value = rate,
                        starSize = 16.dp
                    )
                    Text(
                        text = "(${rate.toTrimString()})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = StarColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            if (comment.isNotBlank()) {
                SelectionContainer {
                    Text(
                        text = comment,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }
            }
        }
    }
}
