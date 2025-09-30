package com.xiaoyv.bangumi.features.mono.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.mono.browser.business.MonoBrowserEvent
import com.xiaoyv.bangumi.features.mono.browser.business.MonoBrowserState
import com.xiaoyv.bangumi.features.mono.browser.business.MonoBrowserViewModel
import com.xiaoyv.bangumi.features.mono.page.MonoPageRoute
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.list.ListMonoType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun MonoBrowserRoute(
    viewModel: MonoBrowserViewModel = koinViewModel<MonoBrowserViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    MonoBrowserScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is MonoBrowserEvent.UI.OnNavUp -> onNavUp()
                is MonoBrowserEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun MonoBrowserScreen(
    baseState: BaseState<MonoBrowserState>,
    onUiEvent: (MonoBrowserEvent.UI) -> Unit,
    onActionEvent: (MonoBrowserEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = baseState.payload?.title?.let { stringResource(it) },
                onNavigationClick = { onUiEvent(MonoBrowserEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(MonoBrowserEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            MonoPageRoute(
                param = remember(state.param) {
                    ListMonoParam(
                        type = ListMonoType.BROWSER,
                        browser = state.param,
                    )
                },
                onNavUp = { onUiEvent(MonoBrowserEvent.UI.OnNavUp) },
                onNavScreen = { screen -> onUiEvent(MonoBrowserEvent.UI.OnNavScreen(screen)) },
                header = { MonoBrowserScreenHeader(state, onUiEvent, onActionEvent) },
            )
        }
    }
}

@Composable
private fun MonoBrowserScreenHeader(
    state: MonoBrowserState,
    onUiEvent: (MonoBrowserEvent.UI) -> Unit,
    onActionEvent: (MonoBrowserEvent.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(vertical = LayoutPaddingHalf, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MonoBrowserChipGroup(
            modifier = Modifier.fillMaxWidth(),
            title = "排序",
            current = state.param.orderBy,
            items = state.sortFilters,
            onClick = { onActionEvent(MonoBrowserEvent.Action.OnChangeFilterOrderBy(it.type)) }
        )
        MonoBrowserChipGroup(
            modifier = Modifier.fillMaxWidth(),
            title = "类型",
            current = state.param.mutexParam.type,
            items = state.typeFilters,
            onClick = { onActionEvent(MonoBrowserEvent.Action.OnChangeFilterType(it.type)) }
        )

        MonoBrowserChipGroup(
            modifier = Modifier.fillMaxWidth(),
            title = "血型",
            current = state.param.mutexParam.gender,
            items = state.genderFilters,
            onClick = { onActionEvent(MonoBrowserEvent.Action.OnChangeFilterGender(it.type)) }
        )

        MonoBrowserChipGroup(
            modifier = Modifier.fillMaxWidth(),
            title = "性别",
            current = state.param.mutexParam.bloodType,
            items = state.bloodFilters,
            onClick = { onActionEvent(MonoBrowserEvent.Action.OnChangeFilterBloodType(it.type)) }
        )

        MonoBrowserChipGroup(
            modifier = Modifier.fillMaxWidth(),
            title = "出生月份",
            current = state.param.mutexParam.month,
            items = state.monthFilters,
            onClick = { onActionEvent(MonoBrowserEvent.Action.OnChangeFilterMonth(it.type)) }
        )
    }
}

@Composable
private fun MonoBrowserChipGroup(
    title: String,
    current: String?,
    items: SerializeList<ComposeTextTab<String>>,
    modifier: Modifier = Modifier,
    onClick: (ComposeTextTab<String>) -> Unit = {},
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = items.indexOfFirst { it.type == current }.coerceAtLeast(0)
    )

    LaunchedEffect(current) {
        if (!current.isNullOrBlank()) {
            listState.animateScrollToItem(items.indexOfFirst { it.type == current }.coerceAtLeast(0))
        }
    }

    LazyRow(
        modifier = modifier,
        state = listState,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stickyHeader {
            Text(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 4.dp)
                    .padding(end = 12.dp),
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        items(items) {
            FilterChip(
                modifier = Modifier.padding(start = 4.dp, end = 12.dp),
                selected = current == it.type,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                ),
                label = {
                    Text(
                        text = it.displayText(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                onClick = { onClick(it) }
            )
        }
    }
}

