@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Density
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import org.jetbrains.skia.EncodedImageFormat
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * 验证实际弹层背景、卡片及确认按钮的光栅颜色，防止深色主题层次丢失。
 */
class MiuixControlsVisualTest {
    @Test
    fun sheetCardsAndDestructiveButtonsKeepTheirSemanticColorsInLightAndDark() {
        for (dark in listOf(false, true)) {
            var snapshot = ComposeControlsSnapshot()
            val settings = ComposeSetting(
                ui = ComposeSetting.UIConfig(
                    style = SettingUiStyle.MIUIX,
                    theme = if (dark) SettingTheme.DARK else SettingTheme.LIGHT,
                ),
            )
            val scene = ImageComposeScene(width = 390, height = 420, density = Density(1f)) {
                CompositionLocalProvider(LocalSharedState provides SharedState(settings = settings)) {
                    BgmAppTheme(modifier = Modifier.fillMaxSize()) {
                        val sheetColor = BgmBottomSheetDefaults.containerColor()
                        snapshot = snapshot.copy(
                            sheet = sheetColor,
                            card = MiuixTheme.colorScheme.surfaceContainer,
                            error = MiuixTheme.colorScheme.error,
                            onError = MiuixTheme.colorScheme.onError,
                            primary = MiuixTheme.colorScheme.primary,
                            onPrimary = MiuixTheme.colorScheme.onPrimary,
                            filter = MiuixTheme.colorScheme.surfaceContainerHigh,
                        )
                        Column(
                            Modifier.fillMaxSize().background(sheetColor).padding(ContentMargin),
                            verticalArrangement = Arrangement.spacedBy(ContentMargin),
                        ) {
                            Text("自定义首页")
                            Card(
                                Modifier.fillMaxWidth().onGloballyPositioned {
                                    snapshot = snapshot.copy(cardBounds = it.boundsInRoot())
                                },
                            ) {
                                Row(
                                    Modifier.fillMaxWidth().padding(ContentMargin),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("番剧日历")
                                    Checkbox(state = ToggleableState.On, onClick = {})
                                }
                            }
                            BgmFilterChip(
                                label = "高级过滤",
                                onClick = {},
                                modifier = Modifier.onGloballyPositioned {
                                    snapshot = snapshot.copy(filterBounds = it.boundsInRoot())
                                },
                            )
                            BgmConfirmationButton(
                                text = "删除",
                                onClick = {},
                                isDestructive = true,
                                modifier = Modifier.onGloballyPositioned {
                                    snapshot = snapshot.copy(errorBounds = it.boundsInRoot())
                                },
                            )
                            BgmConfirmationButton(
                                text = "确认",
                                onClick = {},
                                modifier = Modifier.onGloballyPositioned {
                                    snapshot = snapshot.copy(primaryBounds = it.boundsInRoot())
                                },
                            )
                        }
                    }
                }
            }
            try {
                repeat(24) { scene.render(it * 16_666_667L).close() }
                scene.render(400_000_000L).use { image ->
                    requireNotNull(image.encodeToData(EncodedImageFormat.PNG)).use { encoded ->
                        val bitmap = ImageIO.read(ByteArrayInputStream(encoded.bytes))
                        assertNotEquals(snapshot.sheet, snapshot.card)
                        assertEquals(snapshot.sheet.toArgb(), bitmap.getRGB(0, 200))
                        assertFill(bitmap, snapshot.cardBounds, snapshot.card)
                        assertFill(bitmap, snapshot.errorBounds, snapshot.error)
                        assertFill(bitmap, snapshot.primaryBounds, snapshot.primary)
                        assertFill(bitmap, snapshot.filterBounds, snapshot.filter)
                        assertTrue(countPixels(bitmap, snapshot.errorBounds, snapshot.onError) > 5)
                        assertTrue(countPixels(bitmap, snapshot.primaryBounds, snapshot.onPrimary) > 5)
                        val output = File("build/reports/miuix-visual/controls-${if (dark) "dark" else "light"}.png")
                        output.parentFile.mkdirs()
                        output.writeBytes(encoded.bytes)
                    }
                }
            } finally {
                scene.close()
            }
        }
    }

    private fun assertFill(bitmap: BufferedImage, bounds: Rect, color: Color) {
        assertEquals(color.toArgb(), bitmap.getRGB(bounds.left.toInt() + 24, bounds.top.toInt() + 8))
    }

    private fun countPixels(bitmap: BufferedImage, bounds: Rect, color: Color): Int {
        var count = 0
        for (y in bounds.top.toInt() until bounds.bottom.toInt()) {
            for (x in bounds.left.toInt() until bounds.right.toInt()) {
                if (bitmap.getRGB(x, y) == color.toArgb()) count++
            }
        }
        return count
    }
}

@Immutable
private data class ComposeControlsSnapshot(
    val sheet: Color = Color.Unspecified,
    val card: Color = Color.Unspecified,
    val error: Color = Color.Unspecified,
    val onError: Color = Color.Unspecified,
    val primary: Color = Color.Unspecified,
    val onPrimary: Color = Color.Unspecified,
    val filter: Color = Color.Unspecified,
    val cardBounds: Rect = Rect.Zero,
    val errorBounds: Rect = Rect.Zero,
    val primaryBounds: Rect = Rect.Zero,
    val filterBounds: Rect = Rect.Zero,
)
