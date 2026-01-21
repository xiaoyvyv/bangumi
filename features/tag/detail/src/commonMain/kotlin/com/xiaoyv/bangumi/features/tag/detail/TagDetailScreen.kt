package com.xiaoyv.bangumi.features.tag.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_search
import com.xiaoyv.bangumi.core_resource.resources.global_tag
import com.xiaoyv.bangumi.features.tag.detail.business.TagDetailEvent
import com.xiaoyv.bangumi.features.tag.detail.business.TagDetailState
import com.xiaoyv.bangumi.features.tag.detail.business.TagDetailViewModel
import com.xiaoyv.bangumi.features.tag.page.TagPageRoute
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.list.ListTagType
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.ListTagParam
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.subjectTypeTabs
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TagDetailRoute(
    viewModel: TagDetailViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    TagDetailScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TagDetailEvent.UI.OnNavUp -> onNavUp()
                is TagDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TagDetailScreen(
    baseState: BaseState<TagDetailState>,
    onUiEvent: (TagDetailEvent.UI) -> Unit,
    onActionEvent: (TagDetailEvent.Action) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.global_tag),
                actions = {
                    IconButton(onClick = { onUiEvent(TagDetailEvent.UI.OnNavScreen(Screen.SearchInput())) }) {
                        Icon(
                            imageVector = BgmIcons.Search,
                            contentDescription = stringResource(Res.string.global_search)
                        )
                    }
                },
                onNavigationClick = { onUiEvent(TagDetailEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(TagDetailEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            TagDetailScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun TagDetailScreenContent(
    state: TagDetailState,
    onUiEvent: (TagDetailEvent.UI) -> Unit,
    onActionEvent: (TagDetailEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = subjectTypeTabs,
        initialPage = subjectTypeTabs.indexOfFirst { it.type == state.type }.coerceAtLeast(0)
    ) {
        TagPageRoute(
            param = remember(state.type) {
                ListTagParam(
                    type = ListTagType.BROWSER,
                    subjectType = subjectTypeTabs[it].type
                )
            },
            onNavScreen = { screen ->
                onUiEvent(TagDetailEvent.UI.OnNavScreen(screen))
            }
        )
    }
}

