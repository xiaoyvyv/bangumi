package androidx.compose.ui.window

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.LocalAwtWindow
import java.awt.Window

/**
 * Compose Multiplatform 1.12.0 兼容垫片。
 *
 * MediaMP 0.3.2 依赖旧版 Compose Desktop 的 [LocalWindow] 来获取当前的 AWT [Window]。
 * 在 1.12.0 中，JetBrains 将其正式标准化并公开为 [LocalAwtWindow]，系统默认会在每个窗口内部自动 provide。
 * 此处将其直接重定向至 [LocalAwtWindow]，无需上层手动注入即可自动感知底层 Window。
 */
@OptIn(ExperimentalComposeUiApi::class)
val LocalWindow: ProvidableCompositionLocal<Window?>
    get() = LocalAwtWindow
