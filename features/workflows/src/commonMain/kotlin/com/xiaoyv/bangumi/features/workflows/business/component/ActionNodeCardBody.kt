package com.xiaoyv.bangumi.features.workflows.business.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionExecutionStep
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionNodeSpec
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * UE5 / ComfyUI 风格节点主体。
 *
 * 采用经典 Blueprint 左右对分视图：
 * - 左侧：输入 Pin 端口与参数配置 Key，且**参数值直接显示在端口旁边**；
 * - 右侧：输出 Pin 端口与运行期 Output 结果，且**结果值直接显示在输出端口旁边**。
 */
@Composable
fun ActionNodeCardBody(
    node: ActionNode,
    spec: ActionNodeSpec?,
    modifier: Modifier = Modifier,
    step: ActionExecutionStep? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(ContentMargin),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        // 左侧栏：输入控制端口 & 参数配置项
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        ) {
            // 控制输入端口
            spec?.inputPorts?.forEach { port ->
                InputPortRow(portId = port.id, label = "控制输入")
            }

            // 参数配置端口与配置值 (配置值在端口旁边)
            node.config.forEach { (key, value) ->
                InputConfigParamRow(key = key, value = value)
            }
        }

        // 右侧栏：输出控制端口 & 运行结果
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            horizontalAlignment = Alignment.End,
        ) {
            // 控制输出端口
            spec?.outputPorts?.forEach { port ->
                val isActive = step?.outputPortId == port.id
                OutputPortRow(portId = port.id, isActive = isActive)
            }

            // 如果有执行输出数据，显示在输出侧
            if (step != null && step.output.isNotEmpty()) {
                step.output.forEach { (outputKey, outputVal) ->
                    OutputDataRow(key = outputKey, value = outputVal)
                }
            }
        }
    }
}

/**
 * 左侧：控制输入 Pin 端口行。
 */
@Composable
private fun InputPortRow(
    portId: String,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Pin 圆点
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )

        Text(
            text = portId,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace,
        )
    }
}

/**
 * 左侧：参数 Pin 端口行，**参数值直接显示在端口旁边**。
 */
@Composable
private fun InputConfigParamRow(
    key: String,
    value: JsonElement,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // 数据 Pin 圆点
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary),
        )

        // 参数 Key
        Text(
            text = key,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace,
        )

        // 参数值 (显示在端口旁边)
        val displayValue = if (value is JsonPrimitive) value.content else value.toString()
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(
                text = displayValue,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * 右侧：控制输出 Pin 端口行。
 */
@Composable
private fun OutputPortRow(
    portId: String,
    isActive: Boolean,
) {
    val (pinColor, textColor) = when (portId) {
        ActionControlPortId.SUCCESS, ActionControlPortId.TRUE, ActionControlPortId.MATCHED -> {
            if (isActive) MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline to MaterialTheme.colorScheme.onSurface
        }

        ActionControlPortId.FAILURE, ActionControlPortId.FALSE -> {
            if (isActive) MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.outline to MaterialTheme.colorScheme.onSurface
        }

        else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSurface
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(pinColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "已命中",
                    style = MaterialTheme.typography.labelSmall,
                    color = pinColor,
                )
            }
        }

        Text(
            text = portId,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.End,
        )

        // Pin 圆点
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isActive) pinColor else MaterialTheme.colorScheme.outline),
        )
    }
}

/**
 * 右侧：输出数据 Pin 行，**结果值显示在端口旁边**。
 */
@Composable
private fun OutputDataRow(
    key: String,
    value: JsonElement,
) {
    val displayValue = if (value is JsonPrimitive) value.content else value.toString()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // 结果值 (显示在输出端口旁边)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(
                text = displayValue,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = key,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
        )

        // 数据输出 Pin 圆点
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary),
        )
    }
}
