package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 使用当前设计风格的弹层背景，保留弹层与内部卡片的层次。
 */
object BgmBottomSheetDefaults {
    @Composable
    fun containerColor(): Color = if (isMiuixUi()) {
        MiuixTheme.colorScheme.surface
    } else {
        BottomSheetDefaults.ContainerColor
    }
}
