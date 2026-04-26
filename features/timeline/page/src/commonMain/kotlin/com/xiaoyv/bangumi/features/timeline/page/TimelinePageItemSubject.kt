package com.xiaoyv.bangumi.features.timeline.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor


@Composable
internal fun TimelinePageItemSubject(
    item: ComposeTimeline,
    onClick: (ComposeSubject) -> Unit,
) {
    when {
        item.memo.subject.size == 1 -> {
            val entry = item.memo.subject.first()
            val subject = entry.subject

            TimelinePageItemSubjectItem(
                subject = subject,
                onClick = onClick
            )

            // 收藏吐槽或打分
            if (entry.comment.isNotBlank() || entry.rate > 0) {
                TimelinePageItemComment(
                    modifier = Modifier.fillMaxWidth(),
                    comment = entry.comment,
                    rate = entry.rate.toDouble(),
                )
            }
        }

        item.memo.subject.size > 1 -> {
            OutlinedCard {
                LazyRow(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                ) {
                    items(
                        items = item.memo.subject,
                        contentType = { CONTENT_TYPE_TIMELINE_SUBJECT }
                    ) { entry ->
                        InfoImage(
                            modifier = Modifier.width(85.dp),
                            model = entry.subject.images.displayMediumImage,
                            text = entry.subject.displayName,
                            textPadding = 4.dp,
                            textMaxLines = 1,
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                            ),
                            onClick = { onClick(entry.subject) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun TimelinePageItemSubjectItem(
    subject: ComposeSubject,
    onClick: (ComposeSubject) -> Unit,
) {
    OutlinedCard(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxWidth(),
        onClick = { onClick(subject) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
        ) {
            InfoImage(
                modifier = Modifier.width(65.dp),
                model = subject.images.displayMediumImage,
                onClick = { onClick(subject) }
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = subject.displayName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subject.info,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (subject.rating.score > 0) {
                    Text(
                        text = subject.rating.score.toFixed(1).toTrimString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = StarColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}