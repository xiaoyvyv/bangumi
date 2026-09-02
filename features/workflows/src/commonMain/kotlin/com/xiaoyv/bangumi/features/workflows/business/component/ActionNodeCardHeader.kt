package com.xiaoyv.bangumi.features.workflows.business.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionExecutionStatus
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf

/**
 * UE5 / ComfyUI 风格节点 Header 顶栏。
 *
 * 彩色分类背景图层，展示图标、节点名称、Type 标识符与状态 Badge。
 */
@Composable
fun ActionNodeCardHeader(
    node: ActionNode,
    spec: ActionNodeSpec?,
    modifier: Modifier = Modifier,
    status: String? = null,
) {
    val category = spec?.category ?: "other"
    val containerColor = ActionNodeCardStyle.getCategoryContainerColor(category)
    val contentColor = ActionNodeCardStyle.getCategoryContentColor(category)
    val icon = ActionNodeCardStyle.getCategoryIcon(category)
    val label = ActionNodeCardStyle.getCategoryLabel(category)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp),
                )
            }

            Text(
                text = node.label.ifBlank { node.type },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )

            Text(
                text = "(${node.type})",
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace,
            )
        }

        if (!status.isNull_or_blank()) {
            val statusColor = when (status) {
                ActionExecutionStatus.SUCCESS -> MaterialTheme.colorScheme.primary
                ActionExecutionStatus.FAILED -> MaterialTheme.colorScheme.error
                ActionExecutionStatus.CANCELLED -> MaterialTheme.colorScheme.outline
                else -> MaterialTheme.colorScheme.secondary
            }

            SuggestionChip(
                onClick = { },
                label = {
                    Text(
                        text = when (status) {
                            ActionExecutionStatus.SUCCESS -> "已完成"
                            ActionExecutionStatus.FAILED -> "异常"
                            ActionExecutionStatus.CANCELLED -> "取消"
                            else -> status.orEmpty()
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = statusColor.copy(alpha = 0.15f),
                ),
                border = null,
            )
        }
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
