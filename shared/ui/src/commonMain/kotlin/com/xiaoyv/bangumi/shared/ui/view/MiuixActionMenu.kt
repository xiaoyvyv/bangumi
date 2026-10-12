package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_more
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.DropdownDefaults
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowListPopup

/**
 * 使用 Miuix 窗口及列表行显示操作菜单，并保留删除等操作的语义颜色。
 *
 * @param options 可用操作及其颜色。
 * @param current 当前选中值，普通操作菜单可以省略。
 * @param onOptionClick 点击操作后的回调。
 */
@Composable
fun <T : Any> MiuixActionMenu(
    options: SerializeList<ComposeTextTab<T>>,
    imageVector: ImageVector,
    imageTint: Color,
    modifier: Modifier = Modifier,
    current: T? = null,
    onOptionClick: (ComposeTextTab<T>) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val openingFeedback = rememberMenuHapticFeedback()
    Box(modifier) {
        IconButton(onClick = {
            openingFeedback()
            expanded = true
        }) {
            Icon(imageVector, stringResource(Res.string.global_more), tint = imageTint)
        }
        WindowListPopup(
            show = expanded,
            alignment = PopupPositionProvider.Align.End,
            onDismissRequest = { expanded = false },
        ) {
            MiuixActionMenuItems(options, current) { tab ->
                expanded = false
                onOptionClick(tab)
            }
        }
    }
}

/**
 * 使用原生下拉行参与弹层的固有尺寸测量，避免普通设置行在无界宽度下溢出。
 *
 * @param options 菜单操作及其语义颜色。
 * @param current 当前选项，操作菜单可以为空。
 * @param onOptionClick 点击操作后的回调。
 */
@Composable
internal fun <T : Any> MiuixActionMenuItems(
    options: SerializeList<ComposeTextTab<T>>,
    current: T? = null,
    onOptionClick: (ComposeTextTab<T>) -> Unit,
) {
    val selectionFeedback = rememberMenuHapticFeedback(HapticFeedbackType.Confirm)
    ListPopupColumn {
        options.forEachIndexed { index, tab ->
            val color = when {
                tab.type == ButtonType.Delete || tab.type == ButtonType.Report -> MiuixTheme.colorScheme.error
                tab.contentColor != Color.Unspecified -> tab.contentColor
                else -> MiuixTheme.colorScheme.onSurfaceContainer
            }
            DropdownImpl(
                text = tab.displayText(),
                optionSize = options.size,
                isSelected = tab.type == current,
                index = index,
                dropdownColors = DropdownDefaults.dropdownColors(contentColor = color),
                onSelectedIndexChange = {
                    selectionFeedback()
                    onOptionClick(tab)
                },
            )
        }
    }
}
