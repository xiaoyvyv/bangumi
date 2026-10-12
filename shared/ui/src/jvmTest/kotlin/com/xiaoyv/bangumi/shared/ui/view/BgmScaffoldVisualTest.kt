@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import com.xiaoyv.bangumi.shared.ui.view.navigation.FloatingBottomBar
import com.xiaoyv.bangumi.shared.ui.view.navigation.FloatingBottomBarDefaults
import com.xiaoyv.bangumi.shared.ui.view.navigation.FloatingBottomBarItem
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalFloatingBottomBarContentColor
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalMainBottomBarOverlap
import com.xiaoyv.bangumi.shared.ui.view.navigation.floatingContentPadding
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import org.jetbrains.skia.EncodedImageFormat
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * 使用项目实际主题、页面容器和液态底栏进行离屏布局及光栅验证。
 */
class BgmScaffoldVisualTest {

    @Test
    fun liquidBarKeepsContentAndFabAboveItsBoundsInBothThemes() {
        for (style in styles) {
            for (dark in listOf(false, true)) {
                val snapshot = capture(style, dark, hasFab = true, screenshotName = "${styleName(style)}-${if (dark) "dark" else "light"}-liquid")
                assertEquals(0.dp, snapshot.contentPaddingBottom)
                assertEquals(overlap, snapshot.scrollingPaddingBottom)
                assertNear(height.toFloat(), snapshot.viewportBounds.bottom)
                assertNear(height - overlap.value, snapshot.contentEndBounds.bottom)
                assertTrue(snapshot.contentEndBounds.bottom < snapshot.barBounds.top)
                assertTrue(requireNotNull(snapshot.fabBounds).bottom < snapshot.barBounds.top)
                assertEquals(style == SettingUiStyle.MIUIX, snapshot.miuix)
                if (style == SettingUiStyle.MIUIX) {
                    assertEquals(snapshot.miuixPrimary, snapshot.materialPrimary)
                }
            }
        }
    }

    @Test
    fun nestedScaffoldConsumesFloatingBarOverlapOnlyOnce() {
        for (style in styles) {
            val snapshot = capture(style, dark = false, nested = true)
            assertEquals(0.dp, snapshot.contentPaddingBottom)
            assertEquals(0.dp, snapshot.nestedPaddingBottom)
            assertEquals(overlap, snapshot.scrollingPaddingBottom)
            assertNear(height.toFloat(), snapshot.viewportBounds.bottom)
            assertNear(height - overlap.value, snapshot.contentEndBounds.bottom)
        }
    }

    @Test
    fun scrollingViewportAndRowsExtendBehindFloatingBar() {
        for (style in styles) {
            for (dark in listOf(false, true)) {
                val snapshot = capture(
                    style = style,
                    dark = dark,
                    scrollAtEnd = false,
                    screenshotName = "${styleName(style)}-${if (dark) "dark" else "light"}-liquid-scrolling",
                )
                assertEquals(0.dp, snapshot.contentPaddingBottom)
                assertEquals(overlap, snapshot.scrollingPaddingBottom)
                assertNear(height.toFloat(), snapshot.viewportBounds.bottom)
                assertEquals(snapshot.rowColor.toArgb(), snapshot.bottomPixel)
            }
        }
    }

    @Test
    fun emptyFabAndVisibleFabRaiseSnackbarExactlyOnce() {
        for (style in styles) {
            for (hasFab in listOf(false, true)) {
                val base = capture(style, dark = false, overlap = 0.dp, hasFab = hasFab, hasSnackbar = true)
                val raised = capture(style, dark = false, hasFab = hasFab, hasSnackbar = true)
                assertNear(
                    overlap.value,
                    requireNotNull(base.snackbarBounds).bottom - requireNotNull(raised.snackbarBounds).bottom,
                )
                if (hasFab) {
                    assertNear(overlap.value, requireNotNull(base.fabBounds).bottom - requireNotNull(raised.fabBounds).bottom)
                } else {
                    assertEquals(null, raised.fabBounds)
                }
            }
        }
    }

    @Test
    fun monetSeedAndDarkModeUpdateTheActualThemeBridge() {
        for (style in styles) {
            val first = capture(style, dark = false, monetTheme = true, themeColor = 0xFF105AA4)
            val second = capture(style, dark = false, monetTheme = true, themeColor = 0xFFAA3510)
            val dark = capture(style, dark = true, monetTheme = true, themeColor = 0xFF105AA4)
            assertEquals(first.miuixPrimary, first.materialPrimary)
            assertEquals(second.miuixPrimary, second.materialPrimary)
            assertNotEquals(first.materialPrimary, second.materialPrimary)
            assertNotEquals(first.background, dark.background)
        }
    }

    private fun capture(
        @SettingUiStyle style: Int,
        dark: Boolean,
        overlap: Dp = Companion.overlap,
        hasFab: Boolean = false,
        hasSnackbar: Boolean = false,
        nested: Boolean = false,
        monetTheme: Boolean = false,
        themeColor: Long = ComposeSetting.UIConfig.DefaultThemeColor,
        scrollAtEnd: Boolean = true,
        screenshotName: String? = null,
    ): ComposeScaffoldSnapshot {
        var snapshot = ComposeScaffoldSnapshot()
        val settings = ComposeSetting(
            ui = ComposeSetting.UIConfig(
                style = style,
                theme = if (dark) SettingTheme.DARK else SettingTheme.LIGHT,
                monetTheme = monetTheme,
                themeColor = themeColor,
            ),
        )
        val fixture: @Composable (PaddingValues) -> Unit = { padding ->
            FixtureContent(
                padding = padding,
                scrollAtEnd = scrollAtEnd,
                onViewportBounds = { snapshot = snapshot.copy(viewportBounds = it) },
                onScrollingPadding = { snapshot = snapshot.copy(scrollingPaddingBottom = it) },
                onRowColor = { snapshot = snapshot.copy(rowColor = it) },
                onEndBounds = { snapshot = snapshot.copy(contentEndBounds = it) },
            )
        }
        val scene = ImageComposeScene(width = width, height = height, density = Density(1f)) {
            CompositionLocalProvider(LocalSharedState provides SharedState(settings = settings)) {
                BgmAppTheme(modifier = Modifier.fillMaxSize()) {
                    snapshot = snapshot.copy(
                        miuix = isMiuixUi(),
                        materialPrimary = MaterialTheme.colorScheme.primary,
                        miuixPrimary = MiuixTheme.colorScheme.primary,
                        background = MaterialTheme.colorScheme.background,
                    )
                    val backdrop = rememberLayerBackdrop()
                    Box(Modifier.fillMaxSize()) {
                        Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) {
                            CompositionLocalProvider(LocalMainBottomBarOverlap provides overlap) {
                                BgmScaffold(
                                    modifier = Modifier.fillMaxSize(),
                                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                    floatingActionButton = {
                                        if (hasFab) {
                                            BgmFloatingActionButton(
                                                onClick = {},
                                                modifier = Modifier.onGloballyPositioned {
                                                    snapshot = snapshot.copy(fabBounds = it.boundsInRoot())
                                                },
                                            ) {
                                                Icon(BgmIcons.Add, contentDescription = "添加")
                                            }
                                        }
                                    },
                                    snackbarHost = {
                                        if (hasSnackbar) {
                                            Snackbar(
                                                modifier = Modifier.onGloballyPositioned {
                                                    snapshot = snapshot.copy(snackbarBounds = it.boundsInRoot())
                                                },
                                            ) { Text("操作已完成") }
                                        }
                                    },
                                ) { padding ->
                                    snapshot = snapshot.copy(contentPaddingBottom = padding.calculateBottomPadding())
                                    if (nested) {
                                        Box(Modifier.fillMaxSize().padding(padding)) {
                                            BgmScaffold(
                                                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                            ) { innerPadding ->
                                                snapshot = snapshot.copy(nestedPaddingBottom = innerPadding.calculateBottomPadding())
                                                fixture(innerPadding)
                                            }
                                        }
                                    } else {
                                        fixture(padding)
                                    }
                                }
                            }
                        }
                        if (overlap > 0.dp) {
                            FloatingBottomBar(
                                selectedIndex = { 0 },
                                onSelected = {},
                                backdrop = backdrop,
                                tabsCount = 5,
                                colors = FloatingBottomBarDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    activeContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                modifier = Modifier.align(Alignment.BottomCenter)
                                    .padding(bottom = barBottom, start = ContentMargin, end = ContentMargin)
                                    .onGloballyPositioned { snapshot = snapshot.copy(barBounds = it.boundsInRoot()) },
                            ) {
                                val tabs = remember {
                                    listOf(
                                        "首页" to BgmIcons.Home,
                                        "动态" to BgmIcons.DateRange,
                                        "发现" to BgmIcons.Search,
                                        "乐园" to BgmIcons.Favorite,
                                        "我的" to BgmIcons.Person,
                                    )
                                }
                                tabs.forEachIndexed { index, (label, icon) ->
                                    FloatingBottomBarItem(selected = index == 0, onClick = {}) {
                                        if (isMiuixUi()) {
                                            MiuixIcon(icon, contentDescription = null, modifier = Modifier.size(26.dp))
                                            MiuixText(label, style = MiuixTheme.textStyles.footnote2, maxLines = 1)
                                        } else {
                                            Icon(
                                                icon,
                                                contentDescription = null,
                                                modifier = Modifier.size(26.dp),
                                                tint = LocalFloatingBottomBarContentColor.current,
                                            )
                                            Text(
                                                label,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = LocalFloatingBottomBarContentColor.current,
                                                maxLines = 1,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        try {
            repeat(48) { frame -> scene.render(frame * 16_666_667L).close() }
            scene.render(800_000_000L).use { image ->
                requireNotNull(image.encodeToData(EncodedImageFormat.PNG)).use { encoded ->
                    val bitmap = ImageIO.read(ByteArrayInputStream(encoded.bytes))
                    snapshot = snapshot.copy(bottomPixel = bitmap.getRGB(4, height - 4))
                    if (screenshotName != null) {
                        val output = File("build/reports/miuix-visual/$screenshotName.png")
                        output.parentFile.mkdirs()
                        output.writeBytes(encoded.bytes)
                    }
                }
            }
            return snapshot
        } finally {
            scene.close()
        }
    }

    @Composable
    private fun FixtureContent(
        padding: PaddingValues,
        scrollAtEnd: Boolean,
        onViewportBounds: (Rect) -> Unit,
        onScrollingPadding: (Dp) -> Unit,
        onRowColor: (Color) -> Unit,
        onEndBounds: (Rect) -> Unit,
    ) {
        val scrollingPadding = floatingContentPadding(padding)
        onScrollingPadding(scrollingPadding.calculateBottomPadding())
        onRowColor(MaterialTheme.colorScheme.primaryContainer)
        LazyColumn(
            modifier = Modifier.fillMaxSize().onGloballyPositioned { onViewportBounds(it.boundsInRoot()) },
            state = rememberLazyListState(initialFirstVisibleItemIndex = if (scrollAtEnd) 20 else 0),
            contentPadding = scrollingPadding,
        ) {
            items(20) { index ->
                Box(
                    Modifier.fillMaxWidth().height(96.dp).background(
                        if (index % 2 == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("收藏条目 ${index + 1}")
                }
            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .onGloballyPositioned { onEndBounds(it.boundsInRoot()) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("最后一项可见", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }

    private fun assertNear(expected: Float, actual: Float) {
        assertTrue(abs(expected - actual) <= 1f, "Expected $expected px, measured $actual px")
    }

    private fun styleName(@SettingUiStyle style: Int) = if (style == SettingUiStyle.MIUIX) "miuix" else "material3"

    companion object {
        private const val width = 390
        private const val height = 780
        private val barBottom = 32.dp
        private val overlap = 104.dp
        private val styles = listOf(SettingUiStyle.MATERIAL3, SettingUiStyle.MIUIX)
    }
}

@Immutable
private data class ComposeScaffoldSnapshot(
    val contentPaddingBottom: Dp = Dp.Unspecified,
    val nestedPaddingBottom: Dp = Dp.Unspecified,
    val scrollingPaddingBottom: Dp = Dp.Unspecified,
    val viewportBounds: Rect = Rect.Zero,
    val contentEndBounds: Rect = Rect.Zero,
    val rowColor: Color = Color.Unspecified,
    val bottomPixel: Int = 0,
    val fabBounds: Rect? = null,
    val snackbarBounds: Rect? = null,
    val barBounds: Rect = Rect.Zero,
    val miuix: Boolean = false,
    val materialPrimary: Color = Color.Unspecified,
    val miuixPrimary: Color = Color.Unspecified,
    val background: Color = Color.Unspecified,
)
