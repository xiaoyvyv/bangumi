package com.xiaoyv.bangumi.features.workflows.business.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStep
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf

/**
 * 节点运行期输出与异常展示视图。
 *
 * 显示节点执行后的 [ActionExecutionStep] 步骤结果、输出数据 [ActionExecutionStep.output] 或错误日志。
 */
@Composable
fun ActionNodeCardOutput(
    step: ActionExecutionStep,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (step.error != null) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
            .padding(ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (step.error != null) "执行异常:" else "执行结果 [${step.outputPortId.orEmpty()}]:",
                style = MaterialTheme.typography.labelSmall,
                color = if (step.error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )

            val durationMillis = step.finishedAt - step.startedAt
            Text(
                text = "${durationMillis}ms",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                fontFamily = FontFamily.Monospace,
            )
        }

        if (step.error != null) {
            Text(
                text = step.error?.message.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        } else if (step.output.isNotEmpty()) {
            Text(
                text = step.output.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
