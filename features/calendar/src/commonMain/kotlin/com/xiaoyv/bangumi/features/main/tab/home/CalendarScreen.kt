package com.xiaoyv.bangumi.features.main.tab.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.LineStyle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.calendar_today_title
import com.xiaoyv.bangumi.core_resource.resources.calendar_tomorrow_title
import com.xiaoyv.bangumi.features.main.tab.home.business.CalendarEvent
import com.xiaoyv.bangumi.features.main.tab.home.business.CalendarState
import com.xiaoyv.bangumi.features.main.tab.home.business.CalendarViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.currentWeekDay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.calendarWeekDays
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.kts.isExtraSmallScreen
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectCardItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectLineItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun CalendarRoute(
    viewModel: CalendarViewModel = koinViewModel<CalendarViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    CalendarScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is CalendarEvent.UI.OnNavUp -> onNavUp()
                is CalendarEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun CalendarScreen(
    baseState: BaseState<CalendarState>,
    onUiEvent: (CalendarEvent.UI) -> Unit,
    onActionEvent: (CalendarEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = baseState.content { if (isToday) stringResource(Res.string.calendar_today_title) else stringResource(Res.string.calendar_tomorrow_title) },
                actions = {
                    IconButton(onClick = { onActionEvent(CalendarEvent.Action.OnChangeLayoutMode) }) {
                        baseState.content {
                            Icon(
                                imageVector = if (isGrid) BgmIcons.LineStyle else BgmIcons.GridView,
                                contentDescription = null
                            )
                        }
                    }
                },
                onNavigationClick = { onUiEvent(CalendarEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(CalendarEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            CalendarScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun CalendarScreenContent(
    state: CalendarState,
    onUiEvent: (CalendarEvent.UI) -> Unit,
    onActionEvent: (CalendarEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = calendarWeekDays,
        initialPage = currentWeekDay().let { if (state.isToday) it - 1 else if (it == 7) 0 else it }
    ) {
        val type = calendarWeekDays[it].type.toString()
        val sections = state.calendarMap[type].orEmpty()
        CalendarScreenPage(state, sections, onUiEvent, onActionEvent)
    }
}


@Composable
private fun CalendarScreenPage(
    state: CalendarState,
    sections: List<ComposeHomeSection>,
    onUiEvent: (CalendarEvent.UI) -> Unit,
    onActionEvent: (CalendarEvent.Action) -> Unit,
) {
    if (state.isGrid) {
        val gridCells = if (isExtraSmallScreen) GridCells.Fixed(3) else GridCells.Adaptive(100.dp)

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = gridCells,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(sections) {
                SubjectCardItem(
                    modifier = Modifier.fillMaxWidth(),
                    display = remember(it.subject.id) { ComposeSubjectDisplay(it.subject) },
                    style = MaterialTheme.typography.bodyMedium,
                    onClick = { onUiEvent(CalendarEvent.UI.OnNavScreen(Screen.SubjectDetail(it.subject.id))) }
                )
            }
        }
    } else {
        val lazyListState = rememberLazyListState()

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(sections) {
                SubjectLineItem(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = LayoutPadding, vertical = 12.dp),
                    display = remember(it.subject.id) { ComposeSubjectDisplay(it.subject) },
                    onClick = { onUiEvent(CalendarEvent.UI.OnNavScreen(Screen.SubjectDetail(it.subject.id))) }
                )
            }
        }
    }
}
