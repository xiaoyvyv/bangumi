@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import java.io.File
import org.jetbrains.skia.EncodedImageFormat
import top.yukonga.miuix.kmp.utils.WindowNavigationEventScope
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 验证原生搜索栏在有无输入及深浅色下都能容纳返回、清空和提交操作。
 */
class BgmSearchInputBarVisualTest {
    @Test
    fun nativeSearchBarFitsPageHeaderWithAndWithoutQuery() {
        for (dark in listOf(false, true)) {
            for (query in listOf("", "以图识番")) {
                var bounds = Rect.Zero
                val settings = ComposeSetting(
                    ui = ComposeSetting.UIConfig(style = SettingUiStyle.MIUIX, theme = if (dark) SettingTheme.DARK else SettingTheme.LIGHT),
                )
                val scene = ImageComposeScene(width = 390, height = 120, density = Density(1f)) {
                    CompositionLocalProvider(LocalSharedState provides SharedState(settings = settings)) {
                        BgmAppTheme(modifier = Modifier.fillMaxSize()) {
                            WindowNavigationEventScope {
                                Box(Modifier.fillMaxSize().background(MiuixTheme.colorScheme.surface).padding(top = 24.dp)) {
                                    BgmSearchInputBar(
                                        query = query,
                                        label = "搜索",
                                        searchIcon = Icons.Rounded.Search,
                                        backIcon = Icons.AutoMirrored.Rounded.ArrowBack,
                                        backLabel = "返回",
                                        clearLabel = "清空",
                                        clearIcon = Icons.Rounded.Close,
                                        onQueryChange = {},
                                        onSearch = {},
                                        onBack = {},
                                        modifier = Modifier.onGloballyPositioned { bounds = it.boundsInRoot() },
                                    )
                                }
                            }
                        }
                    }
                }
                try {
                    repeat(24) { scene.render(it * 16_666_667L).close() }
                    assertEquals(390f, bounds.width)
                    assertTrue(bounds.height in 45f..64f)
                    scene.render(400_000_000L).use { image ->
                        requireNotNull(image.encodeToData(EncodedImageFormat.PNG)).use { encoded ->
                            val output = File("build/reports/miuix-visual/search-${if (dark) "dark" else "light"}-${if (query.isBlank()) "empty" else "query"}.png")
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
