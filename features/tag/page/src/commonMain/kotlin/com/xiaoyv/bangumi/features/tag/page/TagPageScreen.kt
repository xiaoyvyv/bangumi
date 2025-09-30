package com.xiaoyv.bangumi.features.tag.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.tag.page.business.TagPageEvent
import com.xiaoyv.bangumi.features.tag.page.business.TagPageState
import com.xiaoyv.bangumi.features.tag.page.business.TagPageViewModel
import com.xiaoyv.bangumi.features.tag.page.business.koinTagPageViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.formatShort
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.ListTagParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTag
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyVerticalGrid
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.HighlightedText
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.kts.isExtraSmallScreen
import kotlinx.collections.immutable.persistentListOf
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TAG_ITEM = "CONTENT_TAG_ITEM"


@Composable
fun TagPageRoute(
    param: ListTagParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: TagPageViewModel = koinTagPageViewModel(param)
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.tags.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    TagPageScreen(
        baseState = baseState,
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TagPageEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TagPageScreen(
    baseState: BaseState<TagPageState>,
    pagingItems: LazyPagingItems<ComposeTag>,
    onUiEvent: (TagPageEvent.UI) -> Unit,
    onActionEvent: (TagPageEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(TagPageEvent.Action.OnRefresh(it)) },
        baseState = baseState,
    ) { state ->
        TagPageScreenContent(state, pagingItems, onUiEvent, onActionEvent)
    }
}


@Composable
private fun TagPageScreenContent(
    state: TagPageState,
    pagingItems: LazyPagingItems<ComposeTag>,
    onUiEvent: (TagPageEvent.UI) -> Unit,
    onActionEvent: (TagPageEvent.Action) -> Unit,
) {
    StateLazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        pagingItems = pagingItems,
        columns = if (isExtraSmallScreen) GridCells.Fixed(4) else GridCells.Adaptive(80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = LayoutPaddingHalf, horizontal = LayoutPadding),
        contentType = { CONTENT_TAG_ITEM },
        key = { item, _ -> item.name },
        showScrollUpBtn = true
    ) { item, _ ->
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            onClick = {
                onUiEvent(
                    TagPageEvent.UI.OnNavScreen(
                        Screen.SubjectBrowser(
                            body = SubjectBrowserBody(
                                subjectType = SubjectType.ANIME,
                                sort = SubjectSortBrowserType.TRENDS,
                                tags = persistentListOf(item.name)
                            )
                        )
                    )
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(LayoutPaddingHalf),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HighlightedText(
                    text = item.name,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    highlights = remember(state.keyword) {
                        if (state.keyword.isBlank()) persistentListOf() else persistentListOf(state.keyword)
                    },
                    textAlign = TextAlign.Center,
                    highlightColor = Color.Green.copy(green = 0.8f)
                )
                if (item.count > 0) Text(
                    text = item.count.formatShort(1),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

