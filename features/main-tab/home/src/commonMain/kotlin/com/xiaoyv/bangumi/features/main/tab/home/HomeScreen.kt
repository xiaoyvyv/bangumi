package com.xiaoyv.bangumi.features.main.tab.home

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.app_name
import com.xiaoyv.bangumi.core_resource.resources.global_search
import com.xiaoyv.bangumi.core_resource.resources.ic_logo_riff
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeEvent
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeState
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeViewModel
import com.xiaoyv.bangumi.features.main.tab.home.page.HomeBlogScreen
import com.xiaoyv.bangumi.features.main.tab.home.page.HomeIndexScreen
import com.xiaoyv.bangumi.features.main.tab.home.page.HomeMainScreen
import com.xiaoyv.bangumi.features.main.tab.home.page.group.HomeGroupScreen
import com.xiaoyv.bangumi.features.main.tab.home.page.mono.HomeMonoScreen
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.HomeTab
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.mainHomeTabs
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    HomeScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is HomeEvent.UI.OnNavUp -> onNavUp()
                is HomeEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun HomeScreen(
    baseState: BaseState<HomeState>,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                titleContent = {
                    Icon(
                        modifier = Modifier
                            .height(32.dp)
                            .aspectRatio(27 / 7f),
                        painter = painterResource(Res.drawable.ic_logo_riff),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = stringResource(Res.string.app_name),
                    )
                },
                actions = {
                    if (LocalSharedState.current.isLogin) {
                        Text(
                            modifier = Modifier
                                .alpha(0f)
                                .semantics { contentDescription = "已登录" },
                            text = "已登录",
                        )
                    }
                    IconButton(onClick = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.SearchInput())) }) {
                        Icon(
                            imageVector = BgmIcons.Search,
                            contentDescription = stringResource(Res.string.global_search)
                        )
                    }
                }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            baseState = baseState,
        ) { state ->
            HomeScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun HomeScreenContent(
    state: HomeState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "home_main_pager" },
        tabs = mainHomeTabs,
    ) {
        when (mainHomeTabs[it].type) {
            HomeTab.HOME -> HomeMainScreen(state, onUiEvent, onActionEvent)
            HomeTab.MONO -> HomeMonoScreen(onUiEvent, onActionEvent)
            HomeTab.GROUP -> HomeGroupScreen(state, onUiEvent, onActionEvent)
            HomeTab.INDEX -> HomeIndexScreen(state, onUiEvent, onActionEvent)
            HomeTab.BLOG -> HomeBlogScreen(state, onUiEvent, onActionEvent)
        }
    }
}

