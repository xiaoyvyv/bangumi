package com.xiaoyv.bangumi.features.almanac

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.type_feature_almanac
import com.xiaoyv.bangumi.features.almanac.business.AlmanacEvent
import com.xiaoyv.bangumi.features.almanac.business.AlmanacState
import com.xiaoyv.bangumi.features.almanac.business.AlmanacViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun AlmanacRoute(
    viewModel: AlmanacViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    AlmanacScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is AlmanacEvent.UI.OnNavUp -> onNavUp()
                is AlmanacEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun AlmanacScreen(
    baseState: BaseState<AlmanacState>,
    onUiEvent: (AlmanacEvent.UI) -> Unit,
    onActionEvent: (AlmanacEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.type_feature_almanac),
                onNavigationClick = { onUiEvent(AlmanacEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(AlmanacEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            AlmanacScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun AlmanacScreenContent(
    state: AlmanacState,
    onUiEvent: (AlmanacEvent.UI) -> Unit,
    onActionEvent: (AlmanacEvent.Action) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Adaptive(300.dp),
        verticalArrangement = Arrangement.spacedBy(LayoutPadding),
        horizontalArrangement = Arrangement.spacedBy(LayoutPadding),
        contentPadding = PaddingValues(LayoutPadding)
    ) {
        items(state.almanacs, key = { it.first }, contentType = { "Image" }) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)) {
                Text(
                    text = it.first.toString(),
                    style = MaterialTheme.typography.titleLarge
                )
                StateImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            val url = state.bgmHost + "award/${it.first}"
                            onUiEvent(AlmanacEvent.UI.OnNavScreen(Screen.Web(url)))
                        },
                    model = it.second,
                    shape = MaterialTheme.shapes.small
                )
            }
        }
    }
}

