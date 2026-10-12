@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.skia.EncodedImageFormat
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * 模拟菜单弹层的固有高度测量，覆盖日志中的无界宽度溢出路径。
 */
class MiuixActionMenuVisualTest {
    @Test
    fun destructiveMenuSurvivesIntrinsicMeasurementAndKeepsWarningColor() {
        for (dark in listOf(false, true)) {
            for (fontScale in listOf(1f, 1.5f)) {
                var warning = 0
                val settings = ComposeSetting(
                    ui = ComposeSetting.UIConfig(
                        style = SettingUiStyle.MIUIX,
                        theme = if (dark) SettingTheme.DARK else SettingTheme.LIGHT,
                    ),
                )
                val scene = ImageComposeScene(width = 390, height = 640, density = Density(1f, fontScale)) {
                    CompositionLocalProvider(LocalSharedState provides SharedState(settings = settings)) {
                        BgmAppTheme(modifier = Modifier.fillMaxSize()) {
                            warning = MiuixTheme.colorScheme.error.toArgb()
                            Box(Modifier.height(IntrinsicSize.Min)) {
                                MiuixActionMenuItems(
                                    options = persistentListOf(
                                        ComposeTextTab(ButtonType.Share, labelText = "分享"),
                                        ComposeTextTab(ButtonType.CopyLink, labelText = "复制链接"),
                                        ComposeTextTab(ButtonType.OpenInBrowser, labelText = "使用浏览器打开"),
                                        ComposeTextTab(ButtonType.Delete, labelText = "删除"),
                                    ),
                                    onOptionClick = {},
                                )
                            }
                        }
                    }
                }
                try {
                    repeat(12) { scene.render(it * 16_666_667L).close() }
                    scene.render(200_000_000L).use { image ->
                        requireNotNull(image.encodeToData(EncodedImageFormat.PNG)).use { encoded ->
                            val bitmap = ImageIO.read(ByteArrayInputStream(encoded.bytes))
                            var warningPixels = 0
                            for (y in 0 until bitmap.height) {
                                for (x in 0 until bitmap.width) {
                                    if (bitmap.getRGB(x, y) == warning) warningPixels++
                                }
                            }
                            assertTrue(warningPixels > 5, "删除菜单应保留警告色文字")
                            val output = File("build/reports/miuix-visual/menu-${if (dark) "dark" else "light"}-$fontScale.png")
                            output.parentFile.mkdirs()
                            output.writeBytes(encoded.bytes)
                        }
                    }
                } finally {
                    scene.close()
                }
            }
        }
    }
}
