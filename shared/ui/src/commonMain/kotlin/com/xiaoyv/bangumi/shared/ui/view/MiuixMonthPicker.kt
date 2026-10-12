package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.core_resource.resources.subject_browser_date_select
import com.xiaoyv.bangumi.core_resource.resources.subject_browser_month_unit
import com.xiaoyv.bangumi.core_resource.resources.subject_browser_year_unit
import com.xiaoyv.bangumi.shared.core.utils.currentYear
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.NumberPicker
import top.yukonga.miuix.kmp.basic.NumberPickerDefaults
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog

/**
 * 条目浏览的 Miuix 年月选择弹窗，使用原生数字滚轮与按钮。
 *
 * @param show 是否显示弹窗。
 * @param currentMonth 已保存的月份，0 表示全部。
 * @param currentYear 已保存的年份，0 表示全部。
 * @param onConfirm 保存用户确认的年份和月份。
 * @param onDismiss 取消、确认或外部点击后关闭弹窗。
 * @param wheelHeight 默认字号下滚轮区域的高度。
 * @param wheelVisibleCount 滚轮可见行数，需要为不小于 3 的奇数。
 */
@Composable
fun MiuixMonthPicker(
    show: Boolean,
    currentMonth: Int,
    currentYear: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    wheelHeight: Dp = 200.dp,
    wheelVisibleCount: Int = 5,
) {
    WindowDialog(
        show = show,
        title = stringResource(Res.string.subject_browser_date_select),
        onDismissRequest = onDismiss,
    ) {
        MiuixMonthPickerContent(show, currentMonth, currentYear, onConfirm, onDismiss, wheelHeight, wheelVisibleCount)
    }
}

/**
 * 年月选择的草稿与控件；每次打开恢复当前筛选，仅确认时提交。
 *
 * @param show 弹窗打开时重置取消操作留下的草稿。
 * @param currentMonth 当前月份，0 表示全部。
 * @param currentYear 当前年份，0 表示全部。
 * @param onConfirm 提交年份和月份。
 * @param onDismiss 关闭弹窗。
 * @param wheelHeight 默认字号下滚轮区域的高度。
 * @param wheelVisibleCount 滚轮可见行数。
 */
@Composable
internal fun MiuixMonthPickerContent(
    show: Boolean,
    currentMonth: Int,
    currentYear: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    wheelHeight: Dp,
    wheelVisibleCount: Int,
) {
    var month by rememberSaveable(currentMonth, currentYear) { mutableIntStateOf(currentMonth) }
    var year by rememberSaveable(currentMonth, currentYear) { mutableIntStateOf(currentYear) }
    LaunchedEffect(show) {
        if (show) {
            month = currentMonth
            year = currentYear
        }
    }
    val years = remember {
        buildList {
            add(0)
            addAll((1970..currentYear() + 5).reversed())
        }
    }
    val allLabel = stringResource(Res.string.global_all)
    val yearUnit = stringResource(Res.string.subject_browser_year_unit)
    val monthUnit = stringResource(Res.string.subject_browser_month_unit)
    val selectionFeedback = rememberMenuHapticFeedback(HapticFeedbackType.Confirm)
    val density = LocalDensity.current
    val itemHeight = maxOf(wheelHeight / wheelVisibleCount, NumberPickerDefaults.ItemHeight) * maxOf(1f, density.fontScale)
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val columnWidth = with(density) { ((maxWidth - ContentMargin) / 2).toPx() }
            val pickerStyles = if (density.fontScale > 1f) {
                listOf(MiuixTheme.textStyles.title2, MiuixTheme.textStyles.title3)
            } else {
                listOf(MiuixTheme.textStyles.title1, MiuixTheme.textStyles.title2, MiuixTheme.textStyles.title3)
            }
            val pickerStyle = pickerStyles.firstOrNull { style ->
                textMeasurer.measure("${years[1]}$yearUnit", style.copy(fontWeight = FontWeight.SemiBold), density = density)
                    .multiParagraph.maxIntrinsicWidth <= columnWidth
            } ?: pickerStyles.last()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ContentMargin),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NumberPicker(
                    modifier = Modifier.weight(1f),
                    value = years.indexOf(year).coerceAtLeast(0),
                    onValueChange = { year = years[it] },
                    range = years.indices,
                    visibleItemCount = wheelVisibleCount,
                    itemHeight = itemHeight,
                    textStyle = pickerStyle,
                    label = { if (years[it] == 0) allLabel else "${years[it]}$yearUnit" },
                )
                NumberPicker(
                    modifier = Modifier.weight(1f),
                    value = month,
                    onValueChange = { month = it },
                    range = 0..12,
                    visibleItemCount = wheelVisibleCount,
                    itemHeight = itemHeight,
                    textStyle = pickerStyle,
                    label = { if (it == 0) allLabel else "$it$monthUnit" },
                )
            }
        }
        TextButton(
            text = stringResource(Res.string.global_confirm),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                selectionFeedback()
                onConfirm(year, month)
                onDismiss()
            },
        )
        TextButton(
            text = stringResource(Res.string.global_cancel),
            modifier = Modifier.fillMaxWidth(),
            onClick = onDismiss,
        )
    }
}
