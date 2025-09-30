package com.xiaoyv.bangumi.shared.ui.view.episode

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_topic
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.EpisodeActionMenu
import com.xiaoyv.bangumi.shared.core.types.EpisodeType
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.grouped
import com.xiaoyv.bangumi.shared.ui.component.button.episodeCollectionButtonColors
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmGridPager
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmDefaultIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoneContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDroppedContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionWishContainer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

private fun tab(@EpisodeActionMenu action: Int, label: String) = ComposeTextTab(action, labelText = label)

val episodeOptions: Map<Int, ImmutableList<ComposeTextTab<Int>>> =
    mapOf(
        CollectionEpisodeType.UNKNOWN to persistentListOf(
            tab(EpisodeActionMenu.DONE, "看过"),
            tab(EpisodeActionMenu.SKIP_TO, "看到"),
            tab(EpisodeActionMenu.WISH, "想看"),
            tab(EpisodeActionMenu.DROP, "抛弃"),
        ),
        CollectionEpisodeType.WISH to persistentListOf(
            tab(EpisodeActionMenu.DONE, "看过"),
            tab(EpisodeActionMenu.SKIP_TO, "看到"),
            tab(EpisodeActionMenu.DROP, "抛弃"),
            tab(EpisodeActionMenu.REMOVE, "撤销"),
        ),
        CollectionEpisodeType.DONE to persistentListOf(
            tab(EpisodeActionMenu.DONE, "看过"),
            tab(EpisodeActionMenu.WISH, "想看"),
            tab(EpisodeActionMenu.DROP, "抛弃"),
            tab(EpisodeActionMenu.REMOVE, "撤销"),
        ),
        CollectionEpisodeType.DROPPED to persistentListOf(
            tab(EpisodeActionMenu.DONE, "看过"),
            tab(EpisodeActionMenu.SKIP_TO, "看到"),
            tab(EpisodeActionMenu.WISH, "想看"),
            tab(EpisodeActionMenu.REMOVE, "撤销"),
        ),
    )


@Composable
fun EpisodePager(
    episodes: SerializeList<ComposeEpisode>,
    modifier: Modifier = Modifier,
    maxRows: Int = 5,
    onEpisodeChange: (List<ComposeEpisode>, Int) -> Unit = { _, _ -> },
    onClickEpisode: (ComposeEpisode) -> Unit = {},
) {
    val episodes = remember(episodes) { episodes.grouped() }

    BgmGridPager(
        modifier = modifier,
        items = episodes,
        key = { episodes[it].key },
        maxRows = maxRows,
        contentPadding = PaddingValues(start = LayoutPadding, top = LayoutPaddingHalf, end = LayoutPadding, bottom = LayoutPadding)
    ) {
        val buttonColors = episodeCollectionButtonColors(it.collection.status, it.isAiring, it.isAired)
        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(MaterialTheme.shapes.small)
                .background(if (it.splitter != null) Color.Transparent else buttonColors.containerColor)
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = it.splitter ?: it.sortOrder.toTrimString(),
                color = buttonColors.contentColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal
            )

            if (it.splitter == null) {
                Text(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.TopStart),
                    text = EpisodeType.toAbbrType(it.episodeType),
                    color = buttonColors.contentColor,
                    fontSize = 8.sp,
                    lineHeight = 8.sp,
                    fontWeight = FontWeight.Normal
                )

                EpisodeDropMenu(
                    episodes = episodes,
                    item = it,
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    onEpisodeChange = onEpisodeChange,
                    onClickEpisode = { onClickEpisode(it) }
                )
            }
        }
    }
}

@Composable
fun EpisodeDropMenu(
    episodes: SerializeList<ComposeEpisode>,
    item: ComposeEpisode,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onClickEpisode: () -> Unit,
    onEpisodeChange: (List<ComposeEpisode>, Int) -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
    ) {
        val options = remember(item.collection.status) {
            episodeOptions[item.collection.status] ?: persistentListOf()
        }

        options.forEach {
            val contentColor = when (it.type) {
                EpisodeActionMenu.WISH -> colorCollectionWishContainer
                EpisodeActionMenu.DONE -> colorCollectionDoneContainer
                EpisodeActionMenu.SKIP_TO -> colorCollectionDoneContainer
                EpisodeActionMenu.DROP -> colorCollectionDroppedContainer
                EpisodeActionMenu.REMOVE -> colorCollectionDroppedContainer
                else -> MaterialTheme.colorScheme.onSurface
            }

            DropdownMenuItem(
                onClick = {
                    onDismissRequest()
                    when (it.type) {
                        EpisodeActionMenu.WISH -> onEpisodeChange(listOf(item), CollectionEpisodeType.WISH)
                        EpisodeActionMenu.DONE -> onEpisodeChange(listOf(item), CollectionEpisodeType.DONE)
                        EpisodeActionMenu.DROP -> onEpisodeChange(listOf(item), CollectionEpisodeType.DROPPED)
                        EpisodeActionMenu.REMOVE -> onEpisodeChange(listOf(item), CollectionEpisodeType.UNKNOWN)
                        EpisodeActionMenu.SKIP_TO -> {
                            // 看到
                            val indexOf = episodes.indexOf(item)
                            val ids = if (indexOf != -1) episodes.subList(0, indexOf + 1) else emptyList()
                            onEpisodeChange(ids, CollectionEpisodeType.DONE)
                        }
                    }
                },
                colors = MenuDefaults.itemColors(
                    textColor = contentColor,
                    leadingIconColor = contentColor
                ),
                leadingIcon = {
                    Icon(
                        imageVector = when (it.type) {
                            EpisodeActionMenu.REMOVE -> BgmIconsMirrored.Undo
                            EpisodeActionMenu.WISH -> BgmDefaultIcons.FavoriteBorder
                            EpisodeActionMenu.DONE -> BgmDefaultIcons.Done
                            EpisodeActionMenu.SKIP_TO -> BgmDefaultIcons.DoneAll
                            EpisodeActionMenu.DROP -> BgmDefaultIcons.DeleteSweep
                            else -> BgmIcons.Close
                        },
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = buildAnnotatedString {
                            append(it.displayText())
                            // 看过的条目，菜单后面跟个时间
                            if (item.collection.status == CollectionEpisodeType.DONE
                                && it.type == EpisodeActionMenu.DONE
                                && item.collection.updatedAt > 0
                            ) {
                                append("「")
                                append(item.collection.updatedAt.formatDate("yyyy-MM-dd HH:mm"))
                                append("」")
                            }
                        }
                    )
                }
            )
        }

        DropdownMenuItem(
            onClick = {
                onDismissRequest()
                onClickEpisode()
            },
            leadingIcon = {
                Icon(
                    imageVector = BgmIcons.Tag,
                    contentDescription = stringResource(Res.string.global_topic),
                    tint = Color.Unspecified
                )
            },
            text = {
                Text(
                    text = item.rememberDisplayTitle(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )

        if (content != null) content()
    }
}