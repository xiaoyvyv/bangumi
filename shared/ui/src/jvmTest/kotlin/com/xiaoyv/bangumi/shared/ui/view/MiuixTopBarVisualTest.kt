@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import org.jetbrains.skia.EncodedImageFormat
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 模拟列表在顶栏后方滚动，验证展开、收起和固定顶栏都遮住状态栏区域。
 */
class MiuixTopBarVisualTest {
    @Test
    fun scrollingContentNeverBleedsThroughStatusBarPadding() {
        for (large in listOf(false, true)) {
            val headerColor = Color(0xFF242424)
            var behavior: TopAppBarScrollBehavior? = null
            val settings = ComposeSetting(
                ui = ComposeSetting.UIConfig(style = SettingUiStyle.MIUIX, theme = SettingTheme.DARK),
            )
            val scene = ImageComposeScene(width = 390, height = 780, density = Density(1f)) {
                CompositionLocalProvider(LocalSharedState provides SharedState(settings = settings)) {
                    BgmAppTheme(modifier = Modifier.fillMaxSize()) {
                        Box(Modifier.fillMaxSize().background(Color.Magenta))
                        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                        behavior = scrollBehavior
                        val colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = headerColor,
                            scrolledContainerColor = headerColor,
                        )
                        if (large) {
                            BgmLargeTopAppBar(
                                title = "设置中心",
                                scrollBehavior = scrollBehavior,
                                windowInsets = WindowInsets(0, 24, 0, 0),
                                colors = colors,
                            )
                        } else {
                            BgmTopAppBar(
                                title = "设置中心",
                                windowInsets = WindowInsets(0, 24, 0, 0),
                                colors = colors,
                            )
                        }
                    }
                }
            }
            var frame = 0L
            try {
                repeat(24) { scene.render(frame++ * 16_666_667L).close() }
                for (collapsed in listOf(false, true)) {
                    if (collapsed && large) {
                        requireNotNull(behavior).state.let { it.heightOffset = it.heightOffsetLimit }
                    }
                    repeat(24) { scene.render(frame++ * 16_666_667L).close() }
                    scene.render(frame++ * 16_666_667L).use { image ->
                        requireNotNull(image.encodeToData(EncodedImageFormat.PNG)).use { encoded ->
                            val bitmap = ImageIO.read(ByteArrayInputStream(encoded.bytes))
                            assertEquals(headerColor.toArgb(), bitmap.getRGB(100, 12))
                            val output = File("build/reports/miuix-visual/topbar-${if (large) "large" else "small"}-${if (collapsed) "collapsed" else "expanded"}.png")
                            output.parentFile.mkdirs()
                            output.writeBytes(encoded.bytes)
                        }
                    }
                }
            } finally {
                scene.close()
            }
        }
    }
}
