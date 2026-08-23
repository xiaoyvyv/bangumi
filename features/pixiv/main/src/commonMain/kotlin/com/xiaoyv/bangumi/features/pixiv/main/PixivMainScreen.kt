package com.xiaoyv.bangumi.features.pixiv.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_rank
import com.xiaoyv.bangumi.core_resource.resources.pixiv_logged_in
import com.xiaoyv.bangumi.core_resource.resources.pixiv_visitor_mode
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainEvent
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainState
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainViewModel
import com.xiaoyv.bangumi.features.pixiv.main.page.PixivMainPageScreen
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PixivMainRoute(
    viewModel: PixivMainViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivMainScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivMainEvent.UI.OnNavUp -> onNavUp()
                is PixivMainEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PixivMainScreen(
    uiState: UiState<PixivMainState>,
    onUiEvent: (PixivMainEvent.UI) -> Unit,
    onActionEvent: (PixivMainEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(PixivMainEvent.Action.OnRefresh(loading = true)) }
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = stringResource(Res.string.global_rank),
                    onNavigationClick = { onUiEvent(PixivMainEvent.UI.OnNavUp) },
                    actions = {
                        PixivLoginUserAvatarAction(
                            isLoggedIn = state.isPixivLogin,
                            avatarUrl = state.userAvatar,
                            onClick = { onUiEvent(PixivMainEvent.UI.OnNavScreen(Screen.PixivLogin)) }
                        )
                    }
                )
            }
        ) { paddingValues ->
            PixivMainScreenContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

@Composable
private fun PixivLoginUserAvatarAction(
    isLoggedIn: Boolean,
    avatarUrl: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(end = ContentMarginHalf)
            .size(36.dp)
    ) {
        // 头像主体容器
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (isLoggedIn) {
                if (avatarUrl.isNotBlank()) {
                    StateImage(
                        modifier = Modifier.fillMaxSize(),
                        model = avatarUrl,
                        shape = CircleShape,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(Res.string.pixiv_logged_in),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else {
                // 未登录：游客样式
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = stringResource(Res.string.pixiv_visitor_mode),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 右下角绿色在线 Badge
        if (isLoggedIn) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(10.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}

@Composable
private fun PixivMainScreenContent(
    modifier: Modifier,
    state: PixivMainState,
    onUiEvent: (PixivMainEvent.UI) -> Unit,
    onActionEvent: (PixivMainEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = modifier.fillMaxSize(),
        tabs = state.tabs
    ) { page ->
        PixivMainPageScreen(
            content = state.tabs[page].type,
            onUiEvent = onUiEvent
        )
    }
}

@Composable
@Preview
private fun PreviewPixivMainScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivMainScreen(
            uiState = UiState(PixivMainState()),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
