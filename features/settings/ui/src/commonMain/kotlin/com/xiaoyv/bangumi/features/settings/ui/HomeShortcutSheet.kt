package com.xiaoyv.bangumi.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_restore
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_save
import com.xiaoyv.bangumi.core_resource.resources.settings_home_shortcuts_reorder
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.xiaoyv.bangumi.core_resource.resources.settings_home_shortcuts_sheet
import com.xiaoyv.bangumi.core_resource.resources.settings_home_shortcuts_summary
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialogState
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.ReorderableItem
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.Close
import top.yukonga.miuix.kmp.icon.extended.Ok
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Immutable
data class ComposeHomeShortcutEditItem(
    val id: String,
    val title: String,
    val enabled: Boolean,
)

/**
 * 可勾选、可拖动排序的首页快捷入口弹层。
 *
 * @param state 弹层状态
 * @param items 当前顺序和勾选
 * @param onDismiss 关闭
 * @param onSave 保存后的顺序，未勾选的排在列表里但 enabled 为 false
 */
@Composable
fun HomeShortcutSheet(
    state: BottomSheetDialogState,
    items: List<ComposeHomeShortcutEditItem>,
    defaultItems: List<ComposeHomeShortcutEditItem>,
    onDismiss: () -> Unit,
    onSave: (List<ComposeHomeShortcutEditItem>) -> Unit,
) {
    BottomSheetDialog(
        state = state,
        dragHandle = null,
    ) {
        var localItems by remember(items, state.showing) { mutableStateOf(items) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = MiuixIcons.Basic.Close,
                        contentDescription = stringResource(Res.string.global_cancel),
                        tint = MiuixTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(Res.string.settings_home_shortcuts_sheet),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MiuixTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(Res.string.settings_home_shortcuts_summary),
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                IconButton(onClick = { onSave(localItems) }) {
                    Icon(
                        imageVector = MiuixIcons.Regular.Ok,
                        contentDescription = stringResource(Res.string.global_save),
                        tint = MiuixTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ReorderableColumn(
                    list = localItems,
                    onSettle = { fromIndex, toIndex ->
                        if (fromIndex !in localItems.indices || toIndex !in localItems.indices || fromIndex == toIndex) return@ReorderableColumn
                        localItems = localItems.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
                    },
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) { _, item, isDragging ->
                    key(item.id) {
                        ReorderableItem {
                            HomeShortcutCard(
                                item = item,
                                isDragging = isDragging,
                                dragHandleModifier = Modifier.draggableHandle(),
                                onToggle = {
                                    localItems = localItems.map {
                                        if (it.id == item.id) it.copy(enabled = !it.enabled) else it
                                    }
                                },
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            localItems = defaultItems
                        },
                    cornerRadius = 16.dp,
                    insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    colors = CardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainer),
                ) {
                    Text(
                        text = stringResource(Res.string.global_restore),
                        color = MiuixTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HomeShortcutCard(
    item: ComposeHomeShortcutEditItem,
    isDragging: Boolean,
    dragHandleModifier: Modifier,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        colors = CardDefaults.defaultColors(
            color = if (isDragging) {
                MiuixTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
                MiuixTheme.colorScheme.surfaceContainer
            },
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = dragHandleModifier.size(32.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Icon(
                    imageVector = BgmIcons.Menu,
                    contentDescription = stringResource(Res.string.settings_home_shortcuts_reorder),
                    tint = if (isDragging) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggle,
                    ),
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurface,
            )
            ShortcutCheck(checked = item.enabled, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
private fun ShortcutCheck(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Checkbox(
        state = ToggleableState(checked),
        onClick = { onCheckedChange(!checked) },
    )
}
