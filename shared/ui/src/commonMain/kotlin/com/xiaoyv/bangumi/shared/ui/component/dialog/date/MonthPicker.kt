package com.xiaoyv.bangumi.shared.ui.component.dialog.date

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.shared.core.utils.currentYear
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> WheelPicker(
    items: List<T>,
    selectedItem: T,
    onItemLabel: @Composable (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    visibleCount: Int = 5,
    itemHeight: Dp = 40.dp,
) {
    require(visibleCount % 2 == 1) { "visibleCount should be odd to have a unique center." }

    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }
    val halfVisible = visibleCount / 2

    val initialIndex = items.indexOf(selectedItem).coerceAtLeast(0)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
        initialFirstVisibleItemScrollOffset = 0
    )
    val hapticFeedback = LocalHapticFeedback.current
    val centeredFirstVisibleItemIndex by remember {
        derivedStateOf {
            if (listState.firstVisibleItemScrollOffset > itemHeightPx / 2) {
                listState.firstVisibleItemIndex + 1
            } else {
                listState.firstVisibleItemIndex
            }
        }
    }

    LaunchedEffect(centeredFirstVisibleItemIndex) {
        if (centeredFirstVisibleItemIndex != initialIndex) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onItemSelected(items[centeredFirstVisibleItemIndex])
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrollInProgress ->
                if (!isScrollInProgress) {
                    // 这里避免用户滑动打断 animateScroll 抛出的 CancellationException 终止收集，手动捕获一下
                    try {
                        if (listState.firstVisibleItemScrollOffset > itemHeightPx / 2) {
                            listState.animateScrollToItem(listState.firstVisibleItemIndex + 1, 0)
                        } else {
                            listState.animateScrollToItem(listState.firstVisibleItemIndex, 0)
                        }
                    } catch (_: Exception) {
                    }
                }
            }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            items(halfVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                )
            }
            itemsIndexed(items) { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = onItemLabel(item),
                        textAlign = TextAlign.Center,
                        style = if (index == centeredFirstVisibleItemIndex) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            items(halfVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                )
            }
        }

        // 中心高亮条（可按需要自定义样式）
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
        )
    }
}

@Composable
fun MonthPicker(
    dialogState: AlertDialogState,
    currentMonth: Int,
    currentYear: Int,
    onConfirm: (Int, Int) -> Unit,
    wheelHeight: Dp = 200.dp,
    wheelVisibleCount: Int = 5,
) {
    var month by remember { mutableStateOf(currentMonth) }
    var year by remember { mutableStateOf(currentYear) }
    val years = remember {
        buildList {
            add(0)
            addAll((1970..currentYear() + 5).reversed())
        }
    }
    val months = remember { (0..12).toList() }

    BgmAlertDialog(
        state = dialogState,
        title = {
            Text(
                modifier = Modifier.padding(vertical = 12.dp),
                text = "选择日期"
            )
        },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(wheelHeight),
                horizontalArrangement = Arrangement.spacedBy(LayoutPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WheelPicker(
                    modifier = Modifier.weight(1f),
                    items = years,
                    selectedItem = year,
                    onItemSelected = { year = it },
                    visibleCount = wheelVisibleCount,
                    itemHeight = wheelHeight / wheelVisibleCount,
                    onItemLabel = { if (it == 0) stringResource(Res.string.global_all) else "${it}年" }
                )

                WheelPicker(
                    modifier = Modifier.weight(1f),
                    items = months,
                    selectedItem = month,
                    onItemSelected = { month = it },
                    visibleCount = wheelVisibleCount,
                    itemHeight = wheelHeight / wheelVisibleCount,
                    onItemLabel = { if (it == 0) stringResource(Res.string.global_all) else "${it}月" }
                )
            }
        },
        cancel = {
            TextButton(
                onClick = {
                    // reset
                    month = currentMonth
                    year = currentYear
                    dialogState.dismiss()
                }
            ) {
                Text(text = "取消")
            }
        },
        confirm = {
            TextButton(
                onClick = {
                    onConfirm(year, month)
                    dialogState.dismiss()
                }
            ) {
                Text(text = "确定")
            }
        }
    )
}
