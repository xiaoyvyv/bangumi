package com.xiaoyv.bangumi.features.workflows.business.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallSplit
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.Http
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory

/**
 * 节点分类 UI 样式定义。
 *
 * 为不同类别的节点提供统一的色彩标识、图标和显示文本。
 */
object ActionNodeCardStyle {

    /**
     * 获取指定节点分类的图标。
     */
    fun getCategoryIcon(category: String): ImageVector = when (category.lowercase()) {
        ActionNodeCategory.FLOW -> Icons.Rounded.AccountTree
        ActionNodeCategory.CONTROL -> Icons.AutoMirrored.Rounded.CallSplit
        ActionNodeCategory.LOOP -> Icons.Rounded.Repeat
        ActionNodeCategory.DATA, ActionNodeCategory.OBJECT -> Icons.Rounded.DataObject
        ActionNodeCategory.TEXT -> Icons.Rounded.TextFields
        ActionNodeCategory.ARRAY -> Icons.Rounded.Layers
        ActionNodeCategory.MATH -> Icons.Rounded.Functions
        ActionNodeCategory.URL -> Icons.Rounded.Public
        ActionNodeCategory.CODEC, ActionNodeCategory.CRYPTO -> Icons.Rounded.Lock
        ActionNodeCategory.CSV, ActionNodeCategory.XML, ActionNodeCategory.JSON, ActionNodeCategory.HTML -> Icons.Rounded.Code
        ActionNodeCategory.DATE -> Icons.Rounded.DateRange
        ActionNodeCategory.ACTION -> Icons.Rounded.TouchApp
        ActionNodeCategory.HTTP -> Icons.Rounded.Http
        ActionNodeCategory.STORAGE -> Icons.Rounded.Storage
        ActionNodeCategory.BILIBILI -> Icons.Rounded.Public
        else -> Icons.Rounded.DataObject
    }

    /**
     * 获取指定节点分类的中文名称。
     */
    fun getCategoryLabel(category: String): String = when (category.lowercase()) {
        ActionNodeCategory.FLOW -> "流程控制"
        ActionNodeCategory.CONTROL -> "逻辑分支"
        ActionNodeCategory.LOOP -> "循环控制"
        ActionNodeCategory.DATA -> "数据变量"
        ActionNodeCategory.OBJECT -> "对象字典"
        ActionNodeCategory.TEXT -> "文本处理"
        ActionNodeCategory.ARRAY -> "数组集合"
        ActionNodeCategory.MATH -> "数值计算"
        ActionNodeCategory.URL -> "URL 链接"
        ActionNodeCategory.CODEC -> "编解码"
        ActionNodeCategory.CRYPTO -> "加解密/随机"
        ActionNodeCategory.CSV -> "CSV 数据"
        ActionNodeCategory.XML -> "XML 解析"
        ActionNodeCategory.JSON -> "JSON 数据"
        ActionNodeCategory.HTML -> "HTML 解析"
        ActionNodeCategory.DATE -> "日期时间"
        ActionNodeCategory.ACTION -> "UI/系统动作"
        ActionNodeCategory.HTTP -> "网络请求"
        ActionNodeCategory.STORAGE -> "持久化存储"
        ActionNodeCategory.BILIBILI -> "哔哩哔哩"
        else -> category.uppercase()
    }

    /**
     * 获取节点分类的主调颜色。
     */
    @Composable
    fun getCategoryContainerColor(category: String): Color = when (category.lowercase()) {
        ActionNodeCategory.FLOW -> MaterialTheme.colorScheme.primaryContainer
        ActionNodeCategory.CONTROL, ActionNodeCategory.LOOP -> MaterialTheme.colorScheme.secondaryContainer
        ActionNodeCategory.DATA, ActionNodeCategory.OBJECT -> MaterialTheme.colorScheme.tertiaryContainer
        ActionNodeCategory.ACTION, ActionNodeCategory.HTTP -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    /**
     * 获取节点分类的文字/图标颜色。
     */
    @Composable
    fun getCategoryContentColor(category: String): Color = when (category.lowercase()) {
        ActionNodeCategory.FLOW -> MaterialTheme.colorScheme.onPrimaryContainer
        ActionNodeCategory.CONTROL, ActionNodeCategory.LOOP -> MaterialTheme.colorScheme.onSecondaryContainer
        ActionNodeCategory.DATA, ActionNodeCategory.OBJECT -> MaterialTheme.colorScheme.onTertiaryContainer
        ActionNodeCategory.ACTION, ActionNodeCategory.HTTP -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
