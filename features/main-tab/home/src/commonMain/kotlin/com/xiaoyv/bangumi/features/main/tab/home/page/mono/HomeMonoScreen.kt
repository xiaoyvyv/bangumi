package com.xiaoyv.bangumi.features.main.tab.home.page.mono

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeEvent
import com.xiaoyv.bangumi.shared.core.types.MonoOrderByType
import com.xiaoyv.bangumi.shared.core.utils.ignoreLazyGridContentPadding
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoBrowserBody
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyGridState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import com.xiaoyv.bangumi.shared.ui.kts.HideInPreview
import com.xiaoyv.bangumi.shared.ui.kts.isExtraSmallScreen
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.mono.MonoCardItem
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TYPE_MONO_SECTION = "CONTENT_TYPE_MONO_SECTION"

@Composable
fun HomeMonoScreen(
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) = HideInPreview {
    val viewModel = koinViewModel<HomeMonoViewModel>()
    val baseState by viewModel.collectAsState()

    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = baseState,
        enablePullRefresh = true,
        onRefresh = { viewModel.refresh(it) }
    ) {
        HomeMonoScreenContent(
            state = it,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
    }
}

@Composable
private fun HomeMonoScreenContent(
    state: HomeMonoState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    val gridCells = if (isExtraSmallScreen) GridCells.Fixed(3) else GridCells.Adaptive(120.dp)

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = rememberCacheWindowLazyGridState(),
        columns = gridCells,
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) {
        items(
            items = state.sections,
            key = { it.key + ":" + it.item.key },
            span = { if (it.isHeader) GridItemSpan(maxLineSpan) else GridItemSpan(1) },
            contentType = { CONTENT_TYPE_MONO_SECTION }
        ) {
            HomeMonoScreenSection(
                item = it,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

@Composable
private fun HomeMonoScreenSection(
    item: ComposeSection<ComposeMonoDisplay>,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    if (item.isHeader) {
        SectionTitle(
            modifier = Modifier
                .ignoreLazyGridContentPadding(12.dp)
                .padding(horizontal = ContentMargin, vertical = ContentMargin),
            text = item.header.title,
            action = item.header.more,
            onClick = {
                // 解析板块跳转链接，跳转人物浏览页面，默认按照收藏排序
                val jumpUrl = WebConstant.URL_BASE_WEB + item.header.id.trimStart('/')
                val param = MonoBrowserBody.fromUri(jumpUrl).let {
                    it.copy(orderBy = it.orderBy.ifBlank { MonoOrderByType.TYPE_COLLECT })
                }
                onUiEvent(HomeEvent.UI.OnNavScreen(Screen.MonoBrowser(param.monoType, param)))
            }
        )
    } else {
        MonoCardItem(
            modifier = Modifier.fillMaxWidth(),
            item = item.item,
            onClick = { id, type ->
                onUiEvent(HomeEvent.UI.OnNavScreen(Screen.MonoDetail(id, type)))
            }
        )
    }
}

@Preview
@Composable
private fun PreviewHomeMonoScreenContent() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        HomeMonoScreenContent(
            state = HomeMonoState(),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
