package com.xiaoyv.bangumi.features.timeline.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.timeline_reply_cnt
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TimelinePageItemBlog(
    item: ComposeTimeline,
    onClick: (ComposeBlogEntry) -> Unit,
    contentPaddings: PaddingValues = PaddingValues(12.dp),
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = { onClick(item.memo.blog) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPaddings),
            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
        ) {
            Text(
                text = item.memo.blog.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (item.memo.blog.summary.isNotBlank()) Text(
                text = item.memo.blog.summary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            if (item.memo.blog.replies > 0) Text(
                text = stringResource(Res.string.timeline_reply_cnt, item.memo.blog.replies),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}