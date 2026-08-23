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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
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
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivTag
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
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
private const val CONTENT_TYPE_ILLUST_AUTHOR = "CONTENT_TYPE_ILLUST_AUTHOR"
private const val CONTENT_TYPE_ILLUST_TITLE = "CONTENT_TYPE_ILLUST_TITLE"
private const val CONTENT_TYPE_ILLUST_TAGS = "CONTENT_TYPE_ILLUST_TAGS"
private const val CONTENT_TYPE_ILLUST_STATS = "CONTENT_TYPE_ILLUST_STATS"

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

    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(PixivIllustEvent.Action.OnRefresh(loading = true)) }
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = stringResource(Res.string.pixiv_illust_detail_title),
                    onNavigationClick = { onUiEvent(PixivIllustEvent.UI.OnNavUp) },
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
            }
        ) { paddingValues ->
            PixivIllustScreenContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

@Composable
private fun PixivIllustScreenContent(
    modifier: Modifier,
    state: PixivIllustState,
    onUiEvent: (PixivIllustEvent.UI) -> Unit,
    onActionEvent: (PixivIllustEvent.Action) -> Unit,
) {
    val detail = state.detail

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = ContentMargin * 3)
    ) {
        // 作品原图 / 列表
        if (state.pages.isNotEmpty()) {
            val previewUrls = state.pages.map { page -> page.urls.original.ifBlank { page.urls.regular } }

            itemsIndexed(
                items = state.pages,
                key = { index, _ -> "page_$index" },
                contentType = { _, _ -> CONTENT_TYPE_ILLUST_PAGE }
            ) { index, page ->
                val imageUrl = page.urls.regular.ifBlank { page.urls.original }
                val aspectRatio = if (page.width > 0 && page.height > 0) {
                    page.width.toFloat() / page.height.toFloat()
                } else 1f

                StateImage(
                    model = imageUrl,
                    contentDescription = detail.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .clickable {
                            onUiEvent(
                                PixivIllustEvent.UI.OnNavScreen(
                                    Screen.PreviewMain(index = index, items = previewUrls)
                                )
                            )
                        }
                )
            }
        } else {
            itemKey(unique = CONTENT_TYPE_ILLUST_PAGE) {
                val imageUrl = detail.urls.regular.ifBlank { detail.urls.original }
                val previewUrl = detail.urls.original.ifBlank { detail.urls.regular }
                val aspectRatio = if (detail.width > 0 && detail.height > 0) {
                    detail.width.toFloat() / detail.height.toFloat()
                } else 1f

                StateImage(
                    model = imageUrl,
                    contentDescription = detail.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .clickable {
                            if (previewUrl.isNotBlank()) {
                                onUiEvent(
                                    PixivIllustEvent.UI.OnNavScreen(
                                        Screen.PreviewMain(url = previewUrl)
                                    )
                                )
                            }
                        }
                )
            }
        }

        // 作者信息
        itemKey(unique = CONTENT_TYPE_ILLUST_AUTHOR) {
            PixivIllustAuthorItem(detail = detail)
        }

        // 标题与时间
        itemKey(unique = CONTENT_TYPE_ILLUST_TITLE) {
            PixivIllustTitleItem(detail = detail)
        }

        // 标签 FlowRow
        itemKey(unique = CONTENT_TYPE_ILLUST_TAGS, visible = detail.tags.tags.isNotEmpty()) {
            PixivIllustTagsItem(tags = detail.tags.tags)
        }

        // 作品数据 (浏览/点赞/收藏)
        itemKey(unique = CONTENT_TYPE_ILLUST_STATS) {
            PixivIllustStatsItem(detail = detail)
        }
    }
}

@Composable
private fun PixivIllustAuthorItem(
    detail: ComposePixivIllustDetailBody,
    modifier: Modifier = Modifier,
) {
    val userAvatarUrl = detail.userImageUrl
        .ifBlank { detail.profileImageUrl }
        .ifBlank { detail.userIllusts.values.firstOrNull { it != null && it.profileImageUrl.isNotBlank() }?.profileImageUrl.orEmpty() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (userAvatarUrl.isNotBlank()) {
            AsyncImage(
                model = userAvatarUrl,
                contentDescription = detail.userName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
            )

            Spacer(modifier = Modifier.width(ContentMarginHalf))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detail.userName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            if (detail.userAccount.isNotBlank()) {
                Text(
                    text = "@${detail.userAccount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PixivIllustTitleItem(
    detail: ComposePixivIllustDetailBody,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ContentMargin, vertical = ContentMarginHalf)
    ) {
        Text(
            text = detail.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(ContentMarginHalf / 2))

        Text(
            text = detail.createDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PixivIllustTagsItem(
    tags: List<ComposePixivTag>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
    ) {
        tags.forEach { tag ->
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(horizontal = ContentMarginHalf, vertical = 4.dp)
            ) {
                Text(
                    text = "#${tag.tag}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PixivIllustStatsItem(
    detail: ComposePixivIllustDetailBody,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ContentMargin, vertical = ContentMargin),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${detail.viewCount}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(Res.string.pixiv_illust_stat_views),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${detail.likeCount}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(Res.string.pixiv_illust_stat_likes),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${detail.bookmarkCount}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(Res.string.pixiv_illust_stat_bookmarks),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
