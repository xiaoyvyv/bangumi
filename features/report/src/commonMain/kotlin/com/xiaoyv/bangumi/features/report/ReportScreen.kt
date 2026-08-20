package com.xiaoyv.bangumi.features.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.action_report
import com.xiaoyv.bangumi.core_resource.resources.action_report_attach
import com.xiaoyv.bangumi.core_resource.resources.action_report_select_one
import com.xiaoyv.bangumi.core_resource.resources.action_report_submit
import com.xiaoyv.bangumi.core_resource.resources.action_report_target
import com.xiaoyv.bangumi.core_resource.resources.global_input_hint
import com.xiaoyv.bangumi.features.report.business.ReportEvent
import com.xiaoyv.bangumi.features.report.business.ReportSideEffect
import com.xiaoyv.bangumi.features.report.business.ReportState
import com.xiaoyv.bangumi.features.report.business.ReportViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.ReportReason
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.component.text.textFieldTransparentColors
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ReportRoute(
    viewModel: ReportViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val uiState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {
        when (it) {
            ReportSideEffect.OnReportSuccess -> onNavUp()
        }
    }

    ReportScreen(
        uiState = uiState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is ReportEvent.UI.OnNavUp -> onNavUp()
                is ReportEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun ReportScreen(
    uiState: UiState<ReportState>,
    onUiEvent: (ReportEvent.UI) -> Unit,
    onActionEvent: (ReportEvent.Action) -> Unit
) {
    val state = uiState.data
    val isCanSubmit = when (state.reason) {
        ReportReason.UNKNOWN -> false
        ReportReason.OTHER -> state.comment.text.isNotBlank()
        else -> true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = buildString {
                    append(stringResource(Res.string.action_report_target))
                    append(stringResource(state.title))
                },
                onNavigationClick = { onUiEvent(ReportEvent.UI.OnNavUp) },
                actions = {
                    Button(
                        modifier = Modifier
                            .resetSize()
                            .padding(end = ContentMargin / 2),
                        enabled = isCanSubmit,
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = ContentMargin, vertical = 6.dp),
                        onClick = { onActionEvent(ReportEvent.Action.OnReport) },
                    ) {
                        Text(
                            text = stringResource(Res.string.action_report_submit),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )
        }
    ) { padding ->
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            onRefresh = { loading -> onActionEvent(ReportEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { state ->
            ReportScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}

@Composable
private fun ReportScreenContent(
    state: ReportState,
    onUiEvent: (ReportEvent.UI) -> Unit,
    onActionEvent: (ReportEvent.Action) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.reason) {
        if (state.reason == ReportReason.OTHER) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(ContentMargin),
        verticalArrangement = Arrangement.spacedBy(ContentMargin)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)) {
            Text(
                text = stringResource(Res.string.action_report_select_one),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                TabTokens.reportReasonTabs.forEach { tab ->
                    val isSelected = state.reason == tab.type
                    FilterChip(
                        selected = isSelected,
                        onClick = { onActionEvent(ReportEvent.Action.OnReasonChange(tab.type)) },
                        label = { Text(text = tab.displayText()) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            },
                            labelColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        border = if (isSelected) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        }
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)) {
            Text(
                text = stringResource(Res.string.action_report_attach),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val commentInteractionSource = remember { MutableInteractionSource() }
            val isCommentFocused by commentInteractionSource.collectIsFocusedAsState()

            BmgTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .border(
                        width = 1.dp,
                        color = if (isCommentFocused) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                        shape = MaterialTheme.shapes.medium
                    ),
                value = state.comment,
                onValueChange = { onActionEvent(ReportEvent.Action.OnCommentChange(it)) },
                colors = textFieldTransparentColors(),
                minLines = 6,
                maxLines = 12,
                interactionSource = commentInteractionSource,
                placeholder = {
                    Text(
                        text = stringResource(Res.string.global_input_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            )
        }
    }
}

@Composable
@Preview
private fun PreviewReportScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        ReportScreen(
            uiState = UiState(
                ReportState(title = Res.string.action_report)
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
