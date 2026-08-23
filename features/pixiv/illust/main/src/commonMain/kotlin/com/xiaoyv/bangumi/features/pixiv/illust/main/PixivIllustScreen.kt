package com.xiaoyv.bangumi.features.pixiv.illust.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.pixiv_illust_detail_title
import com.xiaoyv.bangumi.core_resource.resources.pixiv_illust_stat_bookmarks
import com.xiaoyv.bangumi.core_resource.resources.pixiv_illust_stat_likes
import com.xiaoyv.bangumi.core_resource.resources.pixiv_illust_stat_views
import com.xiaoyv.bangumi.features.pixiv.illust.main.business.PixivIllustEvent
import com.xiaoyv.bangumi.features.pixiv.illust.main.business.PixivIllustState
import com.xiaoyv.bangumi.features.pixiv.illust.main.business.PixivIllustViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustDetailBody
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmCollapsingScaffold
import com.xiaoyv.bangumi.shared.ui.component.layout.rememberBgmCollapsingScaffoldState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.itemKey
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.rememberButtonTypeMenu
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TYPE_ILLUST_PAGE = "CONTENT_TYPE_ILLUST_PAGE"

@Composable
fun PixivIllustRoute(
    viewModel: PixivIllustViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivIllustScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivIllustEvent.UI.OnNavUp -> onNavUp()
                is PixivIllustEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PixivIllustScreen(
    uiState: UiState<PixivIllustState>,
    onUiEvent: (PixivIllustEvent.UI) -> Unit,
    onActionEvent: (PixivIllustEvent.Action) -> Unit,
) {
    val actionHandler = LocalActionHandler.current
    val scrollState = rememberScrollState()
    val collapsingState = rememberBgmCollapsingScaffoldState()

    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(PixivIllustEvent.Action.OnRefresh(loading = true)) }
    ) { state ->
        BgmCollapsingScaffold(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            collapsingState = collapsingState,
            topBar = { progressProvider ->
                BgmTopAppBar(
                    title = state.detail.title.ifBlank { stringResource(Res.string.pixiv_illust_detail_title) },
                    onNavigationClick = { onUiEvent(PixivIllustEvent.UI.OnNavUp) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = progressProvider()),
                        titleContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = progressProvider()),
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    actions = {
                        val illustId = state.illustId
                        if (illustId > 0) {
                            DropMenuActionButton(
                                options = rememberButtonTypeMenu {
                                    add(ButtonType.Share)
                                    add(ButtonType.CopyLink)
                                    if (state.detail.title.isNotBlank()) add(ButtonType.CopyName)
                                    add(ButtonType.OpenInBrowser)
                                }
                            ) { item ->
                                val illustUrl = "https://www.pixiv.net/artworks/$illustId"
                                when (item.type) {
                                    ButtonType.Share -> actionHandler.shareContent(illustUrl)
                                    ButtonType.OpenInBrowser -> actionHandler.openInBrowser(illustUrl)
                                    ButtonType.CopyLink -> actionHandler.copyContent(illustUrl)
                                    ButtonType.CopyName -> actionHandler.copyContent(state.detail.title)
                                    else -> Unit
                                }
                            }
                        }
                    }
                )
            },
            collapse = { topPadding ->
                PixivIllustHero(
                    detail = state.detail,
                    page = state.pages.firstOrNull(),
                    topPadding = topPadding,
                    onUserClick = {
                        onUiEvent(PixivIllustEvent.UI.OnNavScreen(Screen.PixivUserMain(state.detail.userId)))
                    },
                    onTagClick = { tag ->
                        onUiEvent(PixivIllustEvent.UI.OnNavScreen(Screen.PixivTag(tag)))
                    },
                )
            },
        ) {
            PixivIllustScreenContent(
                modifier = Modifier,
                state = state,
                onUiEvent = onUiEvent,
            )
        }
    }
}

/**
 * 将封面作品与作品信息组合为可折叠的顶部区域。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PixivIllustHero(
    detail: ComposePixivIllustDetailBody,
    page: com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivPageInfo?,
    topPadding: PaddingValues,
    onUserClick: () -> Unit,
    onTagClick: (String) -> Unit,
) {
    val imageUrl = page?.let { it.urls.regular.ifBlank { it.urls.original } }.orEmpty()
        .ifBlank { detail.urls.regular.ifBlank { detail.urls.original } }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(440.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        StateImage(
            modifier = Modifier.fillMaxSize(),
            model = imageUrl,
            blurLoading = false,
            contentDescription = detail.title,
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface,
                        )
                    )
                )
                .padding(topPadding)
                .padding(
                    start = ContentMargin,
                    top = ContentMargin + ContentMarginHalf,
                    end = ContentMargin,
                    bottom = ContentMargin,
                ),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        ) {
            Text(
                text = detail.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            Text(
                text = detail.userName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.clickable(onClick = onUserClick),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PixivIllustHeaderStat(
                    value = detail.viewCount,
                    label = stringResource(Res.string.pixiv_illust_stat_views),
                )
                PixivIllustHeaderStat(
                    value = detail.likeCount,
                    label = stringResource(Res.string.pixiv_illust_stat_likes),
                )
                PixivIllustHeaderStat(
                    value = detail.bookmarkCount,
                    label = stringResource(Res.string.pixiv_illust_stat_bookmarks),
                )
            }
            if (detail.tags.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    maxLines = 3,
                ) {
                    detail.tags.tags.forEachIndexed { index, tag ->
                        val tagColor = when (index % 4) {
                            0 -> MaterialTheme.colorScheme.primary
                            1 -> MaterialTheme.colorScheme.tertiary
                            2 -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.secondary
                        }

                        Text(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .background(tagColor.copy(alpha = 0.18f))
                                .clickable { onTagClick(tag.tag) }
                                .padding(
                                    horizontal = ContentMarginHalf,
                                    vertical = ContentMarginHalf,
                                ),
                            text = "#${tag.tag}",
                            style = MaterialTheme.typography.labelMedium,
                            color = tagColor,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PixivIllustHeaderStat(
    value: Int,
    label: String,
) {
    Text(
        text = "$value $label",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun PixivIllustScreenContent(
    modifier: Modifier,
    state: PixivIllustState,
    onUiEvent: (PixivIllustEvent.UI) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = ContentMargin),
    ) {
        if (state.pages.isNotEmpty()) {
            val previewUrls = state.pages.map { page -> page.urls.original.ifBlank { page.urls.regular } }
            itemsIndexed(
                items = state.pages,
                key = { index, _ -> "page_$index" },
                contentType = { _, _ -> CONTENT_TYPE_ILLUST_PAGE },
            ) { index, page ->
                val imageUrl = page.urls.regular.ifBlank { page.urls.original }
                val aspectRatio = if (page.width > 0 && page.height > 0) page.width.toFloat() / page.height else 1f

                StateImage(
                    model = imageUrl,
                    contentDescription = state.detail.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .clickable {
                            onUiEvent(
                                PixivIllustEvent.UI.OnNavScreen(
                                    Screen.PreviewMain(index = index, items = previewUrls)
                                )
                            )
                        },
                    blurLoading = false,
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewPixivIllustScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivIllustScreen(
            uiState = UiState(
                data = PixivIllustState(
                    detail = ComposePixivIllustDetailBody(
                        title = "Preview Artwork Title",
                        userName = "Preview Artist",
                        userAccount = "artist_account",
                        createDate = "2025-01-01",
                        viewCount = 1234,
                        likeCount = 567,
                        bookmarkCount = 890
                    )
                )
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
