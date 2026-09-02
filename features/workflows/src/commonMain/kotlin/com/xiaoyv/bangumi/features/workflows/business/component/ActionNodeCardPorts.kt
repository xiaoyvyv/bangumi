package com.xiaoyv.bangumi.features.workflows.business.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionPortSpec
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf

/**
 * 节点输入/输出端口可视化视图。
 *
 * 展示节点的输入控制端口（如 in, body）和输出控制端口（如 next, success, failure, true, false）。
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActionNodeCardPorts(
    spec: ActionNodeSpec?,
    modifier: Modifier = Modifier,
    activeOutputPortId: String? = null,
) {
    if (spec == null) return
    val inputPorts = spec.inputPorts
    val outputPorts = spec.outputPorts

    if (inputPorts.isEmpty() && outputPorts.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) {
        // 输入端口列表
        if (inputPorts.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            ) {
                Text(
                    text = "输入端口:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    inputPorts.forEach { port ->
                        PortChip(port = port, isInput = true)
                    }
                }
            }
        }

        // 输出端口列表
        if (outputPorts.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            ) {
                Text(
                    text = "输出端口:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    outputPorts.forEach { port ->
                        val isActive = activeOutputPortId == port.id
                        PortChip(port = port, isInput = false, isActive = isActive)
                    }
                }
            }
        }
    }
}

@Composable
private fun PortChip(
    port: ActionPortSpec,
    isInput: Boolean,
    isActive: Boolean = false,
) {
    val (bgColor, textColor) = when (port.id) {
        ActionControlPortId.SUCCESS, ActionControlPortId.TRUE, ActionControlPortId.MATCHED -> {
            if (isActive) MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        }

        ActionControlPortId.FAILURE, ActionControlPortId.FALSE -> {
            if (isActive) MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
            else MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        }

        else -> {
            if (isActive) MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
            else MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = port.id,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontFamily = FontFamily.Monospace,
        )
    }
}
