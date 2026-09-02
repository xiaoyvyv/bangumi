package com.xiaoyv.bangumi.features.workflows.business.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNode
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * 节点配置参数展示视图。
 *
 * 将节点的 [ActionNode.config] 格式化展示为名值对，并对插值表达式、JSON 结构体进行代码化高亮展示。
 */
@Composable
fun ActionNodeCardConfig(
    node: ActionNode,
    modifier: Modifier = Modifier,
) {
    if (node.config.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        node.config.forEach { (key, value) ->
            ConfigItemRow(key = key, value = value)
        }
    }
}

@Composable
private fun ConfigItemRow(
    key: String,
    value: JsonElement,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(end = ContentMarginHalf),
        )

        val displayValue = if (value is JsonPrimitive) {
            value.content
        } else {
            value.toString()
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
