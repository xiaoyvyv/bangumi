package com.xiaoyv.bangumi.features.topic.detail

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.topic_ep_info
import com.xiaoyv.bangumi.core_resource.resources.topic_index_info
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailState
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopicDetailScreenSubtitle(state: TopicDetailState) {
    when (state.type) {
        TopicType.TYPE_EP -> Text(
            text = stringResource(Res.string.topic_ep_info, state.episode.duration, state.episode.airdate),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        TopicType.TYPE_SUBJECT,
        TopicType.TYPE_GROUP -> Text(
            text = stringResource(Res.string.topic_index_info, state.topic.createdAt.formatAgo(), state.topic.updatedAt.formatAgo()),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        TopicType.TYPE_INDEX -> Text(
            text = stringResource(Res.string.topic_index_info, state.index.createdAt.formatAgo(), state.index.updatedAt.formatAgo()),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        TopicType.TYPE_BLOG -> Text(
            text = stringResource(Res.string.topic_index_info, state.blog.createdAt.formatAgo(), state.blog.updatedAt.formatAgo()),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}