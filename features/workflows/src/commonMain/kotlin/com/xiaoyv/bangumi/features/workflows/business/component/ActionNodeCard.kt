package com.xiaoyv.bangumi.features.workflows.business.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionExecutionStep
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionNodeSpec
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf

/**
 * UE5 Blueprint / ComfyUI 风格工作流节点卡片组件。
 *
 * 结构说明：
 * - Header：顶部分类主题色 Banner 栏
 * - Body：左右双向分栏（左侧输入 Pin + 参数配置值，右侧输出 Pin + 执行结果值）
 * - Footer：失败异常信息栏（仅在发生错误时展示）
 *
 * @param node 待绘制的工作流节点模型。
 * @param spec 节点规格定义。
 * @param step 节点的运行期执行结果。
 * @param status 节点的运行状态。
 * @param onClick 卡片点击事件。
 */
@Composable
fun ActionNodeCard(
    node: ActionNode,
    modifier: Modifier = Modifier,
    spec: ActionNodeSpec? = null,
    step: ActionExecutionStep? = null,
    status: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // UE5 / ComfyUI 风格彩色 Header
            ActionNodeCardHeader(node = node, spec = spec, status = status)

            // UE5 / ComfyUI 风格左右双栏主体（端口与参数值在同一行展示）
            ActionNodeCardBody(node = node, spec = spec, step = step)

            // 发生异常时展示底部红框 Error Footer
            if (step?.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                        .padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                ) {
                    Text(
                        text = "异常: ${step.error?.message.orEmpty()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }
}
