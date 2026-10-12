package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 与下拉筛选保持同一外观的操作按钮。
 *
 * @param label 按钮文案。
 * @param onClick 打开筛选界面的操作。
 */
@Composable
fun BgmFilterChip(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val openingFeedback = rememberMenuHapticFeedback()
    val onFilterClick: () -> Unit = {
        openingFeedback()
        onClick()
    }
    if (isMiuixUi()) {
        Row(
            modifier = modifier.clip(RoundedCornerShape(50))
                .background(MiuixTheme.colorScheme.surfaceContainerHigh)
                .clickable(role = Role.Button, onClick = onFilterClick)
                .padding(horizontal = ContentMarginHalf * 2, vertical = ContentMarginHalf),
        ) {
            MiuixText(text = label, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
        }
    } else {
        AssistChip(modifier = modifier, onClick = onFilterClick, label = { Text(label) })
    }
}
