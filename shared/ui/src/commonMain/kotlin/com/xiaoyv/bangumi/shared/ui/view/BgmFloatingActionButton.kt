package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.basic.FloatingActionButton as MiuixFloatingActionButton
import top.yukonga.miuix.kmp.theme.LocalContentColor as MiuixContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 页面主操作的悬浮按钮，使用当前风格及主色上的配对图标色。
 *
 * @param onClick 点击主操作的回调。
 */
@Composable
fun BgmFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (isMiuixUi()) {
        MiuixFloatingActionButton(onClick = onClick, modifier = modifier) {
            CompositionLocalProvider(
                LocalContentColor provides MiuixTheme.colorScheme.onPrimary,
                MiuixContentColor provides MiuixTheme.colorScheme.onPrimary,
                content = content,
            )
        }
    } else {
        FloatingActionButton(onClick = onClick, modifier = modifier, content = content)
    }
}
