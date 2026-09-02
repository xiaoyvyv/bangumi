package com.xiaoyv.bangumi.features.workflows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.workflow_example_run
import com.xiaoyv.bangumi.core_resource.resources.workflow_run_empty_log
import com.xiaoyv.bangumi.core_resource.resources.workflow_run_error
import com.xiaoyv.bangumi.core_resource.resources.workflow_run_finished
import com.xiaoyv.bangumi.core_resource.resources.workflow_run_nodes
import com.xiaoyv.bangumi.core_resource.resources.workflow_run_opened_url
import com.xiaoyv.bangumi.core_resource.resources.workflow_run_output
import com.xiaoyv.bangumi.core_resource.resources.workflow_run_running
import com.xiaoyv.bangumi.core_resource.resources.workflow_run_title
import com.xiaoyv.bangumi.core_resource.resources.workflow_saved_title
import com.xiaoyv.bangumi.core_resource.resources.workflow_tab_all
import com.xiaoyv.bangumi.core_resource.resources.workflow_tab_data
import com.xiaoyv.bangumi.core_resource.resources.workflow_tab_flow_control
import com.xiaoyv.bangumi.core_resource.resources.workflow_tab_practice
import com.xiaoyv.bangumi.core_resource.resources.workflow_tab_saved
import com.xiaoyv.bangumi.core_resource.resources.workflow_tab_text_net
import com.xiaoyv.bangumi.core_resource.resources.workflow_tab_ui_side_effect
import com.xiaoyv.bangumi.core_resource.resources.workflow_title
import com.xiaoyv.bangumi.core_resource.resources.workflow_view_execution_log
import com.xiaoyv.bangumi.core_resource.resources.workflow_view_node_graph
import com.xiaoyv.bangumi.features.workflows.business.WorkflowExampleRunState
import com.xiaoyv.bangumi.features.workflows.business.WorkflowSamples
import com.xiaoyv.bangumi.features.workflows.business.WorkflowsEvent
import com.xiaoyv.bangumi.features.workflows.business.WorkflowsSideEffect
import com.xiaoyv.bangumi.features.workflows.business.WorkflowsState
import com.xiaoyv.bangumi.features.workflows.business.WorkflowsViewModel
import com.xiaoyv.bangumi.features.workflows.business.component.ActionWorkflowNodeList
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionExecutionStatus
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.workflow.WorkflowSideEffectHost
import com.xiaoyv.bangumi.shared.ui.component.workflow.rememberWorkflowSideEffectHostState
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun WorkflowsRoute(
    viewModel: WorkflowsViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val uiState by viewModel.collectAsState()
    val workflowSideEffectHostState = rememberWorkflowSideEffectHostState()
    val scope = rememberCoroutineScope()

    viewModel.collectBaseSideEffect { effect ->
        when (effect) {
            is WorkflowsSideEffect.ExecuteAction -> {
                scope.launch {
                    val result = workflowSideEffectHostState.dispatch(effect.effect)
                    viewModel.onEvent(WorkflowsEvent.Action.OnSideEffectResult(effect.id, result))
                }
            }
        }
    }

    WorkflowSideEffectHost(
        hostState = workflowSideEffectHostState,
    )

    WorkflowsScreen(
        uiState = uiState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is WorkflowsEvent.UI.OnNavUp -> onNavUp()
                is WorkflowsEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun WorkflowsScreen(
    uiState: UiState<WorkflowsState>,
    onUiEvent: (WorkflowsEvent.UI) -> Unit,
    onActionEvent: (WorkflowsEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.workflow_title),
                onNavigationClick = { onUiEvent(WorkflowsEvent.UI.OnNavUp) },
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { loading -> onActionEvent(WorkflowsEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { state ->
            WorkflowsScreenContent(state, onActionEvent)
        }
    }
}

private enum class WorkflowCategory {
    ALL,
    PRACTICE,
    FLOW_CONTROL,
    DATA_MATH,
    TEXT_NET,
    UI_SIDE_EFFECT,
    SAVED;

    fun matches(workflow: ActionWorkflow, isSaved: Boolean): Boolean {
        if (isSaved) {
            return this == ALL || this == SAVED
        }
        return when (this) {
            ALL -> true
            SAVED -> false
            PRACTICE -> workflow.id in setOf(
                "subject_tags_to_toast",
                "bilibili_wbi_search",
                "mangadex_search",
                "hanime_search",
            )

            FLOW_CONTROL -> workflow.nodes.any { it.type.startsWith("flow.") || it.type.startsWith("control.") }
            DATA_MATH -> workflow.nodes.any {
                it.type.startsWith("data.") || it.type.startsWith("object.") ||
                        it.type.startsWith("array.") || it.type.startsWith("math.")
            }

            TEXT_NET -> workflow.nodes.any {
                it.type.startsWith("text.") || it.type.startsWith("html.") ||
                        it.type.startsWith("json.") || it.type.startsWith("xml.") ||
                        it.type.startsWith("csv.") || it.type.startsWith("url.") ||
                        it.type.startsWith("crypto.") || it.type.startsWith("codec.") ||
                        it.type.startsWith("date.")
            }

            UI_SIDE_EFFECT -> workflow.nodes.any {
                it.type.startsWith("action.") || it.type.startsWith("ui.") ||
                        it.type.startsWith("system.") || it.type.startsWith("storage.") ||
                        it.type.startsWith("bilibili.")
            }
        }
    }
}

@Composable
private fun WorkflowsScreenContent(
    state: WorkflowsState,
    onActionEvent: (WorkflowsEvent.Action) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isHeightBounded = maxHeight != Dp.Infinity
        var selectedCategory by remember { mutableStateOf(WorkflowCategory.ALL) }

        Column(modifier = Modifier.fillMaxSize()) {
            // 上半屏：测试工作流分类 TabRow 与列表
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isHeightBounded) Modifier.weight(1.2f) else Modifier.height(320.dp),
                    ),
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedCategory.ordinal,
                    edgePadding = ContentMargin,
                    divider = { HorizontalDivider() },
                ) {
                    WorkflowCategory.entries.forEach { category ->
                        val tabText = when (category) {
                            WorkflowCategory.ALL -> stringResource(Res.string.workflow_tab_all)
                            WorkflowCategory.PRACTICE -> stringResource(Res.string.workflow_tab_practice)
                            WorkflowCategory.FLOW_CONTROL -> stringResource(Res.string.workflow_tab_flow_control)
                            WorkflowCategory.DATA_MATH -> stringResource(Res.string.workflow_tab_data)
                            WorkflowCategory.TEXT_NET -> stringResource(Res.string.workflow_tab_text_net)
                            WorkflowCategory.UI_SIDE_EFFECT -> stringResource(Res.string.workflow_tab_ui_side_effect)
                            WorkflowCategory.SAVED -> stringResource(Res.string.workflow_tab_saved)
                        }
                        Tab(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            text = { Text(text = tabText, style = MaterialTheme.typography.titleSmall) }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    val savedWorkflows = if (selectedCategory.matches(WorkflowSamples.all.first(), isSaved = true)) {
                        state.workflows
                    } else emptyList()

                    if (savedWorkflows.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                            text = stringResource(Res.string.workflow_saved_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        savedWorkflows.forEach { workflow ->
                            ListItem(
                                headlineContent = { Text(workflow.name, style = MaterialTheme.typography.titleSmall) },
                                supportingContent = {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(workflow.description, style = MaterialTheme.typography.bodySmall)
                                        Text(
                                            text = workflow.nodes.joinToString(" → ") { it.label.ifBlank { it.type } },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                            )
                            HorizontalDivider()
                        }
                    }

                    val filteredSamples = WorkflowSamples.all.filter {
                        selectedCategory.matches(it, isSaved = false)
                    }

                    filteredSamples.forEach { sample ->
                        ListItem(
                            modifier = Modifier.clickable { onActionEvent(WorkflowsEvent.Action.OnRunSample(sample.id)) },
                            headlineContent = { Text(sample.name, style = MaterialTheme.typography.titleSmall) },
                            supportingContent = {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(sample.description, style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = sample.nodes.joinToString(" → ") { it.label.ifBlank { it.type } },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            },
                            trailingContent = {
                                FilledTonalButton(onClick = { onActionEvent(WorkflowsEvent.Action.OnRunSample(sample.id)) }) {
                                    Text(stringResource(Res.string.workflow_example_run))
                                }
                            },
                        )
                        HorizontalDivider()
                    }
                }
            }

            // 中间分割线
            HorizontalDivider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // 下半屏：运行状态与节点卡片/日志绘制面板
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isHeightBounded) Modifier.weight(2.8f) else Modifier.height(480.dp),
                    ),
            ) {
                WorkflowExampleRunPanel(state.exampleRun)
            }
        }
    }
}

/**
 * 显示示例运行期间的节点卡片面板或完整执行日志。
 *
 * @param run 示例最近一次的运行状态。
 */
@Composable
private fun WorkflowExampleRunPanel(
    run: WorkflowExampleRunState?,
    nodeRegistry: ActionNodeRegistry = koinInject(),
) {
    var selectedPanelTab by remember { mutableStateOf(0) } // 0: 节点卡片, 1: 运行日志

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) {
        // 头部：标题与切换 Chip 组
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentMargin),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.workflow_run_title),
                style = MaterialTheme.typography.titleMedium,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf)) {
                FilterChip(
                    selected = selectedPanelTab == 0,
                    onClick = { selectedPanelTab = 0 },
                    label = { Text(stringResource(Res.string.workflow_view_node_graph)) },
                )
                FilterChip(
                    selected = selectedPanelTab == 1,
                    onClick = { selectedPanelTab = 1 },
                    label = { Text(stringResource(Res.string.workflow_view_execution_log)) },
                )
            }
        }

        if (run == null) {
            Text(
                modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                text = stringResource(Res.string.workflow_run_empty_log),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return
        }

        when (selectedPanelTab) {
            0 -> {
                // 节点卡片视图
                val currentWorkflow = run.workflowId?.let { WorkflowSamples.find(it) }
                if (currentWorkflow != null) {
                    ActionWorkflowNodeList(
                        workflow = currentWorkflow,
                        nodeRegistry = nodeRegistry,
                    )
                } else {
                    Text(
                        modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                        text = stringResource(Res.string.workflow_run_empty_log),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            1 -> {
                // 运行日志面板
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = ContentMargin),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                ) {
                    // 状态 Chip
                    val (statusText, statusContainerColor, statusContentColor) = when (run.status) {
                        WORKFLOW_RUN_STATUS_RUNNING -> Triple(
                            "⏳ " + stringResource(Res.string.workflow_run_running),
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        )

                        ActionExecutionStatus.SUCCESS -> Triple(
                            "✅ " + stringResource(Res.string.workflow_run_finished),
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer,
                        )

                        else -> Triple(
                            "❌ " + stringResource(Res.string.workflow_run_error),
                            MaterialTheme.colorScheme.errorContainer,
                            MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = statusContainerColor),
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                            text = statusText,
                            style = MaterialTheme.typography.labelLarge,
                            color = statusContentColor,
                        )
                    }

                    if (run.executedNodeIds.isNotEmpty()) {
                        Text(
                            text = stringResource(
                                Res.string.workflow_run_nodes,
                                run.executedNodeIds.joinToString(" → "),
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    run.resolvedUrl?.let { url ->
                        Text(
                            text = stringResource(Res.string.workflow_run_opened_url, url),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    run.errorMessage?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    if (run.outputLines.isNotEmpty()) {
                        Text(
                            text = stringResource(Res.string.workflow_run_output),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            SelectionContainer {
                                Column(
                                    modifier = Modifier.padding(ContentMargin),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    run.outputLines.forEach { line ->
                                        Text(
                                            text = line,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private const val WORKFLOW_RUN_STATUS_RUNNING = "running"

@Composable
@Preview
private fun PreviewWorkflowsScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        WorkflowsScreen(
            uiState = UiState(
                WorkflowsState()
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
