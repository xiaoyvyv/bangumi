package com.xiaoyv.bangumi.shared.ui.view.episode

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.episode_action_done
import com.xiaoyv.bangumi.core_resource.resources.episode_action_drop
import com.xiaoyv.bangumi.core_resource.resources.episode_action_remove
import com.xiaoyv.bangumi.core_resource.resources.episode_action_skip_to
import com.xiaoyv.bangumi.core_resource.resources.episode_action_wish
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
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmDefaultIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoneContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDroppedContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionWishContainer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.floor
import kotlin.math.roundToInt

private fun tab(@EpisodeActionMenu action: Int, label: StringResource) = ComposeTextTab(action, label = label)

private val episodeNumberAutoSize = TextAutoSize.StepBased(
    minFontSize = 6.sp,
    maxFontSize = 12.sp,
    stepSize = 0.5.sp,
)

val episodeOptions: Map<Int, ImmutableList<ComposeTextTab<Int>>> =
    mapOf(
        CollectionEpisodeType.UNKNOWN to persistentListOf(
            tab(EpisodeActionMenu.DONE, Res.string.episode_action_done),
            tab(EpisodeActionMenu.SKIP_TO, Res.string.episode_action_skip_to),
            tab(EpisodeActionMenu.WISH, Res.string.episode_action_wish),
            tab(EpisodeActionMenu.DROP, Res.string.episode_action_drop),
        ),
        CollectionEpisodeType.WISH to persistentListOf(
            tab(EpisodeActionMenu.DONE, Res.string.episode_action_done),
            tab(EpisodeActionMenu.SKIP_TO, Res.string.episode_action_skip_to),
            tab(EpisodeActionMenu.DROP, Res.string.episode_action_drop),
            tab(EpisodeActionMenu.REMOVE, Res.string.episode_action_remove),
        ),
        CollectionEpisodeType.DONE to persistentListOf(
            tab(EpisodeActionMenu.DONE, Res.string.episode_action_done),
            tab(EpisodeActionMenu.WISH, Res.string.episode_action_wish),
            tab(EpisodeActionMenu.DROP, Res.string.episode_action_drop),
            tab(EpisodeActionMenu.REMOVE, Res.string.episode_action_remove),
        ),
        CollectionEpisodeType.DROPPED to persistentListOf(
            tab(EpisodeActionMenu.DONE, Res.string.episode_action_done),
            tab(EpisodeActionMenu.SKIP_TO, Res.string.episode_action_skip_to),
            tab(EpisodeActionMenu.WISH, Res.string.episode_action_wish),
            tab(EpisodeActionMenu.REMOVE, Res.string.episode_action_remove),
        ),
    )

@Composable
@OptIn(ExperimentalGridApi::class)
fun EpisodeGrid(
    episodes: SerializeList<ComposeEpisode>,
    modifier: Modifier = Modifier,
    minItemSize: Dp = 32.dp,
    @IntRange(from = 1) maxRows: Int = 10,
    verticalSpacing: Dp = ContentMarginHalf,
    horizontalSpacing: Dp = ContentMarginHalf,
    contentPadding: PaddingValues = PaddingValues(
        start = ContentMargin,
        top = ContentMarginHalf,
        end = ContentMargin,
        bottom = ContentMargin
    ),
    onEpisodeChange: (List<ComposeEpisode>, Int) -> Unit = { _, _ -> },
    onClickEpisode: (ComposeEpisode) -> Unit = {},
) {
    BoxWithConstraints(modifier = modifier) {
        val screenWidth = maxWidth -
                contentPadding.calculateStartPadding(LocalLayoutDirection.current) -
                contentPadding.calculateEndPadding(LocalLayoutDirection.current)

        val columns = floor(screenWidth / (minItemSize + horizontalSpacing))
            .roundToInt()
            .coerceAtLeast(1)
        val horizontalSpacingCount = columns - 1
        val itemSize = (screenWidth - horizontalSpacing * horizontalSpacingCount) / columns
        val items = episodes.take(columns * maxRows)

        Grid(
            config = {
                repeat(columns) { column(minmax(0.dp, 1.fr)) }
                gap(row = verticalSpacing, column = horizontalSpacing)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            items.forEach {
                key(it.key) {
                    Box(modifier = Modifier.size(itemSize)) {
                        EpisodeCell(
                            episodes = episodes,
                            item = it,
                            onEpisodeChange = onEpisodeChange,
                            onClickEpisode = onClickEpisode,
                            textStyle = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

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
        contentPadding = PaddingValues(start = ContentMargin, top = ContentMarginHalf, end = ContentMargin, bottom = ContentMargin)
    ) {
        EpisodeCell(
            episodes = episodes,
            item = it,
            onEpisodeChange = onEpisodeChange,
            onClickEpisode = onClickEpisode,
            textStyle = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun BoxScope.EpisodeCell(
    episodes: SerializeList<ComposeEpisode>,
    item: ComposeEpisode,
    onEpisodeChange: (List<ComposeEpisode>, Int) -> Unit,
    onClickEpisode: (ComposeEpisode) -> Unit,
    textStyle: TextStyle,
) {
    val buttonColors = episodeCollectionButtonColors(item.collection.status, item.isAiring, item.isAired)
    val isSplitter = item.splitter != null
    val showTypeBadge = !isSplitter && item.episodeType != EpisodeType.TYPE_MAIN
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(MaterialTheme.shapes.small)
            .background(if (isSplitter) MaterialTheme.colorScheme.secondaryContainer else buttonColors.containerColor)
            .border(1.dp, if (isSplitter) Color.Transparent else buttonColors.borderColor, MaterialTheme.shapes.small)
            .clickable(enabled = !isSplitter) { expanded = true },
        contentAlignment = Alignment.Center
    ) {
        if (showTypeBadge) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = EpisodeType.toAbbrType(item.episodeType),
                    color = buttonColors.contentColor,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.sp,
                        lineHeight = 10.sp
                    ),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                )
                Text(
                    text = item.sortOrder.toTrimString(),
                    color = buttonColors.contentColor,
                    style = textStyle,
                    autoSize = episodeNumberAutoSize,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Text(
                text = item.splitter ?: item.sortOrder.toTrimString(),
                color = if (isSplitter) MaterialTheme.colorScheme.onSecondaryContainer else buttonColors.contentColor,
                style = if (isSplitter) MaterialTheme.typography.labelMedium else textStyle,
                autoSize = if (isSplitter) null else episodeNumberAutoSize,
                fontWeight = if (isSplitter) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!isSplitter) {
            EpisodeDropMenu(
                episodes = episodes,
                item = item,
                expanded = expanded,
                onDismissRequest = { expanded = false },
                onEpisodeChange = onEpisodeChange,
                onClickEpisode = { onClickEpisode(item) }
            )
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
                    text = item.displayTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )

        if (content != null) content()
    }
}

@Preview
@Composable
fun EpisodeGridPreview() {
    val episodes = remember {
        (1..24).map {
            ComposeEpisode(
                id = it.toLong(),
                sortOrder = it.toDouble(),
                collection = ComposeEpisode.EpCollection(
                    status = if (it <= 5) CollectionEpisodeType.DONE
                    else if (it <= 8) CollectionEpisodeType.WISH
                    else if (it <= 10) CollectionEpisodeType.DROPPED
                    else CollectionEpisodeType.UNKNOWN
                ),
                airdate = "2023-01-01",
            )
        }.toPersistentList()
    }

    PreviewColumn {
        EpisodeGrid(episodes = episodes)
    }
}
