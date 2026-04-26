package com.xiaoyv.bangumi.features.timeline.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.timeline_member_cnt
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import org.jetbrains.compose.resources.stringResource


@Composable
internal fun TimelinePageItemDaily(
    item: ComposeTimeline,
    onClickGroup: (ComposeGroup) -> Unit,
    onClickUser: (ComposeUser) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
    ) {
        if (item.memo.daily.users.isNotEmpty()) {
            items(
                items = item.memo.daily.users,
                contentType = { CONTENT_TYPE_TIMELINE_DAILY }
            ) { user ->
                OutlinedCard(onClick = { onClickUser(user) }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                    ) {
                        StateImage(
                            modifier = Modifier
                                .size(44.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small),
                            shape = MaterialTheme.shapes.small,
                            model = user.avatar.displayGridImage
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = user.nickname,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = user.sign.ifBlank { "@" + user.username },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        if (item.memo.daily.groups.isNotEmpty()) {
            items(
                items = item.memo.daily.groups,
                contentType = { CONTENT_TYPE_TIMELINE_DAILY }
            ) { group ->
                OutlinedCard(onClick = { onClickGroup(group) }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                    ) {
                        StateImage(
                            modifier = Modifier
                                .size(44.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small),
                            shape = MaterialTheme.shapes.small,
                            model = group.images.displayGridImage
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = group.title,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(Res.string.timeline_member_cnt, group.members),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}