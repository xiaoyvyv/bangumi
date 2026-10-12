package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults as MaterialButtonDefaults
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 确认操作按钮，删除等破坏性操作使用警告色及其配对前景色。
 *
 * @param isDestructive 是否会删除数据或撤销已有内容。
 * @param onClick 用户确认操作的回调。
 */
@Composable
fun BgmConfirmationButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
) {
    if (isMiuixUi()) {
        MiuixTextButton(
            text = text,
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            colors = if (isDestructive) {
                ButtonDefaults.textButtonColors(
                    color = MiuixTheme.colorScheme.error,
                    textColor = MiuixTheme.colorScheme.onError,
                )
            } else {
                ButtonDefaults.textButtonColorsPrimary()
            },
        )
    } else {
        TextButton(
            onClick = onClick,
            modifier = modifier,
            colors = MaterialButtonDefaults.textButtonColors(
                contentColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            ),
        ) { Text(text) }
    }
}
