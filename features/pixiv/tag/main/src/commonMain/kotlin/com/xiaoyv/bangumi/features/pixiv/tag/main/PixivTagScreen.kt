package com.xiaoyv.bangumi.features.pixiv.tag.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.pixiv.illust.page.IllustPageRoute
import com.xiaoyv.bangumi.features.pixiv.tag.main.business.PixivTagEvent
import com.xiaoyv.bangumi.features.pixiv.tag.main.business.PixivTagState
import com.xiaoyv.bangumi.features.pixiv.tag.main.business.PixivTagViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.list.ListIllustType
import com.xiaoyv.bangumi.shared.data.model.request.list.pixiv.IllustSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.pixiv.ListIllustParam
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivTagInfoBody
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmCollapsingScaffold
import com.xiaoyv.bangumi.shared.ui.component.layout.rememberCollapsingScaffoldState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.orbitmvi.orbit.compose.collectAsState

/**
 * Connects the Pixiv tag route to its MVI state and navigation callbacks.
 */
@Composable
fun PixivTagRoute(
    viewModel: PixivTagViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivTagScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivTagEvent.UI.OnNavUp -> onNavUp()
                is PixivTagEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

/**
 * Displays a compact tag header above nested type and sort pagers.
 */
@Composable
private fun PixivTagScreen(
    uiState: UiState<PixivTagState>,
    onUiEvent: (PixivTagEvent.UI) -> Unit,
    onActionEvent: (PixivTagEvent.Action) -> Unit,
) {
    val scrollState = rememberScrollState()
    val collapsingState = rememberCollapsingScaffoldState()

    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(PixivTagEvent.Action.OnRefresh(loading = true)) },
    ) { state ->
        BgmCollapsingScaffold(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            collapsingState = collapsingState,
            topBar = { progressProvider ->
                BgmTopAppBar(
                    title = state.tagInfo.tag.ifBlank { state.tag },
                    onNavigationClick = { onUiEvent(PixivTagEvent.UI.OnNavUp) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = progressProvider()),
                        titleContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = progressProvider()),
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            },
            collapse = { topPadding ->
                PixivTagHeader(
                    tag = state.tag,
                    tagInfo = state.tagInfo,
                    topPadding = topPadding,
                )
            },
        ) {
            BgmTabHorizontalPager(
                modifier = Modifier.fillMaxSize(),
                tabs = TabTokens.pixivTagTypeTabs,
            ) { page ->
                val artworkType = TabTokens.pixivTagTypeTabs[page].type
                BgmChipHorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    tabs = TabTokens.pixivTagSortTabs,
                ) { sortPage ->
                    val order = TabTokens.pixivTagSortTabs[sortPage].type
                    val param = remember(state.tag, artworkType, order) {
                        ListIllustParam(
                            type = ListIllustType.SEARCH,
                            ui = PageUI(gridLayout = true),
                            search = IllustSearchBody(
                                keyword = state.tag,
                                artworkType = artworkType,
                                order = order,
                            ),
                        )
                    }
                    IllustPageRoute(
                        param = param,
                        onNavScreen = { onUiEvent(PixivTagEvent.UI.OnNavScreen(it)) },
                    )
                }
            }
        }
    }
}

/**
 * Renders Pixiv tag metadata in the collapsible page header.
 */
@Composable
private fun PixivTagHeader(
    tag: String,
    tagInfo: ComposePixivTagInfoBody,
    topPadding: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        StateImage(
            modifier = Modifier.fillMaxSize(),
            model = tagInfo.thumbnail,
            contentDescription = tagInfo.tag.ifBlank { tag },
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.24f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            MaterialTheme.colorScheme.surface,
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(topPadding)
                .padding(ContentMargin),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        ) {
            Text(
                text = "#${tagInfo.tag.ifBlank { tag }}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (tagInfo.en.tag.isNotBlank()) {
                Text(
                    text = tagInfo.en.tag,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (tagInfo.abstract.isNotBlank()) {
                Text(
                    text = tagInfo.abstract,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewPixivTagScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivTagScreen(
            uiState = UiState(PixivTagState(tag = "初音未来")),
            onUiEvent = {},
            onActionEvent = {},
        )
    }
}
