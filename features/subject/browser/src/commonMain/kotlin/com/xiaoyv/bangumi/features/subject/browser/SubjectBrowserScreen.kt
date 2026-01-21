package com.xiaoyv.bangumi.features.subject.browser

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.LineStyle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_date
import com.xiaoyv.bangumi.core_resource.resources.global_kind
import com.xiaoyv.bangumi.core_resource.resources.global_search
import com.xiaoyv.bangumi.core_resource.resources.global_sort
import com.xiaoyv.bangumi.core_resource.resources.type_path_browser
import com.xiaoyv.bangumi.features.subject.browser.business.SubjectBrowserEvent
import com.xiaoyv.bangumi.features.subject.browser.business.SubjectBrowserState
import com.xiaoyv.bangumi.features.subject.browser.business.SubjectBrowserViewModel
import com.xiaoyv.bangumi.features.subject.page.SubjectPageRoute
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalHideNavIcon
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.date.MonthPicker
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.subjectBrowserSortTabs
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SubjectBrowserRoute(
    viewModel: SubjectBrowserViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SubjectBrowserScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SubjectBrowserEvent.UI.OnNavUp -> onNavUp()
                is SubjectBrowserEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SubjectBrowserScreen(
    baseState: BaseState<SubjectBrowserState>,
    onUiEvent: (SubjectBrowserEvent.UI) -> Unit,
    onActionEvent: (SubjectBrowserEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = baseState.content {
                    buildString {
                        if (title.isNotBlank()) {
                            append(title)
                        } else {
                            append(stringResource(Res.string.type_path_browser))
                            append(" - ")
                            append(stringResource(SubjectSortBrowserType.string(param.browser.sort)))
                            if (param.browser.tags.isNotEmpty()) {
                                append(" - ")
                                append(param.browser.tags.joinToString("，"))
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onActionEvent(SubjectBrowserEvent.Action.OnChangeLayoutMode) }) {
                        baseState.content {
                            Icon(
                                imageVector = if (param.ui.gridLayout) BgmIcons.LineStyle else BgmIcons.GridView,
                                contentDescription = null
                            )
                        }
                    }
                    IconButton(onClick = { onUiEvent(SubjectBrowserEvent.UI.OnNavScreen(Screen.SearchInput())) }) {
                        Icon(
                            imageVector = BgmIcons.Search,
                            contentDescription = stringResource(Res.string.global_search)
                        )
                    }
                },
                navigationIcon = if (LocalHideNavIcon.current) null else {
                    {
                        IconButton(onClick = { onUiEvent(SubjectBrowserEvent.UI.OnNavUp) }) {
                            Icon(
                                imageVector = BgmIconsMirrored.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(SubjectBrowserEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            SubjectBrowserScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SubjectBrowserScreenContent(
    state: SubjectBrowserState,
    onUiEvent: (SubjectBrowserEvent.UI) -> Unit,
    onActionEvent: (SubjectBrowserEvent.Action) -> Unit,
) {
    val param = state.param

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = LayoutPaddingHalf, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_kind),
                current = param.browser.subjectType,
                options = TabTokens.subjectTypeTabs,
                onOptionClick = {
                    onActionEvent(
                        SubjectBrowserEvent.Action.OnUpdateBrowserSubjectParam(
                            body = param.browser.copy(subjectType = it.type)
                        )
                    )
                }
            )

            // 是否隐藏排序过滤项，比如需要固定显示某些排序时，需要隐藏
            if (!state.param.browser.hideSortFilter) DropMenuChip(
                labelPrefix = stringResource(Res.string.global_sort),
                current = param.browser.sort,
                options = subjectBrowserSortTabs.let {
                    // 由于带标签的数据是通过搜索接口实现的，这里暂时不支持发售日过滤
                    if (state.param.browser.tags.isNotEmpty()) it.filter { type -> type.type != SubjectSortBrowserType.DATE }
                        .toPersistentList() else it
                },
                onOptionClick = {
                    onActionEvent(
                        SubjectBrowserEvent.Action.OnUpdateBrowserSubjectParam(
                            body = param.browser.copy(sort = it.type)
                        )
                    )
                }
            )

            // 是否隐藏日期过滤项，比如需要固定显示某些日期时，需要隐藏
            if (!state.param.browser.hideDateFilter) {
                val yearPickerDialogState = rememberAlertDialogState()

                MonthPicker(
                    dialogState = yearPickerDialogState,
                    currentMonth = param.browser.month,
                    currentYear = param.browser.year,
                    onConfirm = { year, month ->
                        onActionEvent(
                            SubjectBrowserEvent.Action.OnUpdateBrowserSubjectParam(
                                body = param.browser.copy(year = year, month = month)
                            )
                        )
                    }
                )

                AssistChip(
                    onClick = { yearPickerDialogState.show() },
                    colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    label = {
                        Text(
                            text = buildString {
                                append(stringResource(Res.string.global_date))
                                append(" ")

                                val year = param.browser.year
                                val month = param.browser.month
                                if (year == 0 && month == 0) {
                                    append(stringResource(Res.string.global_all))
                                } else {
                                    if (year > 0) append("${year}年")
                                    if (month > 0) append("${month}月")
                                }
                            }
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            SubjectPageRoute(
                param = param,
                onNavScreen = {
                    onUiEvent(SubjectBrowserEvent.UI.OnNavScreen(it))
                }
            )
        }
    }
}



