package com.xiaoyv.bangumi.shared.ui.view.magnet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_anime
import com.xiaoyv.bangumi.core_resource.resources.magnet_kind
import com.xiaoyv.bangumi.core_resource.resources.magnet_size
import com.xiaoyv.bangumi.core_resource.resources.magnet_team
import com.xiaoyv.bangumi.core_resource.resources.magnet_time
import com.xiaoyv.bangumi.core_resource.resources.mikan_resource_copy_from
import com.xiaoyv.bangumi.core_resource.resources.mikan_resource_copy_link
import com.xiaoyv.bangumi.core_resource.resources.mikan_resource_copy_torrent
import com.xiaoyv.bangumi.core_resource.resources.mikan_resource_detail
import com.xiaoyv.bangumi.core_resource.resources.mikan_resource_open_way
import com.xiaoyv.bangumi.core_resource.resources.mikan_resource_preview
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.utils.magnetHash
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanResource
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertOptionDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun MikanMagnetItem(
    modifier: Modifier,
    item: ComposeMikanResource,
    checkMode: Boolean = false,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    val actionDialog = rememberAlertDialogState()
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    val uriHandler = LocalUriHandler.current

    AlertOptionDialog(
        state = actionDialog,
        title = stringResource(Res.string.mikan_resource_detail),
        items = remember {
            persistentListOf(
                ComposeTextTab(0, Res.string.mikan_resource_copy_link),
                ComposeTextTab(1, Res.string.mikan_resource_copy_from),
                ComposeTextTab(2, Res.string.mikan_resource_copy_torrent),
                ComposeTextTab(3, Res.string.mikan_resource_open_way),
                ComposeTextTab(4, Res.string.mikan_resource_preview),
            )
        },
        onClick = { tab, index ->
            scope.launch {
                actionDialog.dismiss()
                when (tab.type) {
                    0 -> clipboard.setClipEntry(System.createClipEntry(item.magnet))
                    1 -> clipboard.setClipEntry(System.createClipEntry(item.pageUrl))
                    2 -> clipboard.setClipEntry(System.createClipEntry(item.torrent))
                    3 -> uriHandler.openUri(item.magnet)
                    4 -> {
                        val magnetHash = item.magnet.magnetHash()
                        uriHandler.openUri("https://beta.magnet.pics/m/$magnetHash")
                    }
                }
            }
        }
    )

    OutlinedCard(
        modifier = modifier,
        onClick = { if (checkMode) onCheckedChange(!checked) else actionDialog.show() }
    ) {
        Row(modifier = Modifier.padding(LayoutPadding)) {
            Column(
                modifier = modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                Text(
                    text = item.titleHtml,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                if (item.tags.isNotEmpty()) FlowRow(
                    verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    item.tags.fastForEach {
                        Text(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Text(
                    text = stringResource(Res.string.magnet_team, item.subgroupName.orEmpty()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(Res.string.magnet_time, item.publishDate.orEmpty()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(Res.string.magnet_size, item.fileSize.orEmpty()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(
                        Res.string.magnet_kind,
                        stringResource(Res.string.global_anime)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (checkMode) Checkbox(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = LayoutPadding),
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

