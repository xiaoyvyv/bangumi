@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import java.io.File
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.skia.EncodedImageFormat
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 使用原生弹窗布局离屏验证日期筛选和滚轮，包含开关、取消、重开及确认操作。
 */
class MiuixMonthPickerVisualTest {
    @Test
    fun dateFilterAndPickerRenderInBothThemesWithoutTruncatingDates() {
        for (dark in listOf(false, true)) {
            for (fontScale in listOf(1f, 1.5f)) {
                for (date in listOf(0 to 0, 2022 to 10)) {
                    Harness(dark, fontScale, date.first, date.second).use { harness ->
                        harness.open()
                        val confirm = harness.textNode("确认")
                        val cancel = harness.textNode("取消")
                        assertTrue(confirm.boundsInRoot.width > 280f)
                        assertEquals(confirm.boundsInRoot.width, cancel.boundsInRoot.width)
                        assertTrue(confirm.boundsInRoot.bottom <= cancel.boundsInRoot.top)
                        val expectedYear = if (date.first == 0) "全部" else "${date.first}年"
                        val expectedMonth = if (date.second == 0) "全部" else "${date.second}月"
                        val pickers = harness.pickers()
                        assertEquals(2, pickers.size)
                        assertTrue(pickers[0].config[SemanticsProperties.ContentDescription].first().startsWith(expectedYear))
                        assertTrue(pickers[1].config[SemanticsProperties.ContentDescription].first().startsWith(expectedMonth))
                        val layouts = harness.nodes().mapNotNull { it.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action }
                        harness.save("date-${if (dark) "dark" else "light"}-$fontScale-${date.first}-${date.second}")
                        for (getLayout in layouts) {
                            val results = mutableListOf<TextLayoutResult>()
                            getLayout(results)
                            results.forEach { result ->
                                assertFalse((0 until result.lineCount).any(result::isLineEllipsized), "日期文字被截断：${result.layoutInput.text}")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun cancellingWheelChangesAndReopeningRestoresSavedFilter() {
        Harness(currentYear = 2022, currentMonth = 10).use { harness ->
            harness.open()
            harness.dragMonthUp()
            val draft = harness.pickers()[1].config[SemanticsProperties.ContentDescription].first()
            assertFalse(draft.startsWith("10月"), "拖动应改变月份草稿")
            harness.click("取消")
            harness.frames()
            assertTrue(harness.confirmed.isEmpty())
            harness.open()
            assertTrue(harness.pickers()[1].config[SemanticsProperties.ContentDescription].first().startsWith("10月"))
            harness.click("确认")
            assertEquals(listOf(2022 to 10), harness.confirmed)
            assertTrue(HapticFeedbackType.ContextClick in harness.haptics)
            assertTrue(HapticFeedbackType.Confirm in harness.haptics)
        }
    }

    @Test
    fun confirmingAllKeepsTheZeroSentinelForBothFields() {
        Harness().use { harness ->
            harness.open()
            harness.click("确认")
            assertEquals(listOf(0 to 0), harness.confirmed)
        }
    }

    private class Harness(
        dark: Boolean = false,
        fontScale: Float = 1f,
        currentYear: Int = 0,
        currentMonth: Int = 0,
    ) : AutoCloseable {
        private val showing = mutableStateOf(false)
        val confirmed = mutableListOf<Pair<Int, Int>>()
        val haptics = mutableListOf<HapticFeedbackType>()
        private var frame = 0L
        private val settings = ComposeSetting(ui = ComposeSetting.UIConfig(
            style = SettingUiStyle.MIUIX,
            theme = if (dark) SettingTheme.DARK else SettingTheme.LIGHT,
        ))
        private val scene = ImageComposeScene(width = 390, height = 844, density = Density(1f, fontScale)) {
            CompositionLocalProvider(
                LocalSharedState provides SharedState(settings = settings),
                LocalHapticFeedback provides object : HapticFeedback {
                    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) { haptics += hapticFeedbackType }
                },
            ) {
                BgmAppTheme(modifier = Modifier.fillMaxSize()) {
                    Scaffold {
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(ContentMargin),
                            horizontalArrangement = Arrangement.spacedBy(ContentMargin),
                        ) {
                            DropMenuChip(options = persistentListOf(ComposeTextTab(2, labelText = "动画")), labelPrefix = "类型")
                            BgmFilterChip("日期 全部", onClick = { showing.value = true })
                        }
                        /**
                         * Overlay 与 Window 复用 DialogContentLayout，便于离屏捕获原生弹窗内容。
                         */
                        OverlayDialog(show = showing.value, title = "选择日期", onDismissRequest = { showing.value = false }) {
                            MiuixMonthPickerContent(
                                show = showing.value,
                                currentMonth = currentMonth,
                                currentYear = currentYear,
                                onConfirm = { year, month -> confirmed += year to month },
                                onDismiss = { showing.value = false },
                                wheelHeight = 200.dp,
                                wheelVisibleCount = 5,
                            )
                        }
                    }
                }
            }
        }

        fun frames(count: Int = 40) {
            repeat(count) { scene.render(frame++ * 16_666_667L).close() }
        }

        fun open() {
            frames()
            click("日期 全部")
            frames()
        }

        fun click(text: String) {
            val action = requireNotNull(textNode(text).config.getOrNull(SemanticsActions.OnClick)?.action)
            assertTrue(action())
        }

        fun nodes(): List<SemanticsNode> = scene.semanticsOwners.flatMap { owner ->
            fun walk(node: SemanticsNode): List<SemanticsNode> = listOf(node) + node.children.flatMap(::walk)
            walk(owner.unmergedRootSemanticsNode)
        }

        fun textNode(text: String): SemanticsNode {
            val node = nodes().first {
                it.config.getOrNull(SemanticsProperties.Text)?.any { value -> value.text == text } == true
            }
            return generateSequence(node) { it.parent }.firstOrNull {
                it.config.getOrNull(SemanticsActions.OnClick) != null
            } ?: node
        }

        fun pickers(): List<SemanticsNode> = nodes().filter {
            it.config.getOrNull(SemanticsProperties.ContentDescription)?.any { value -> value.contains(", 0 - ") } == true
        }.sortedBy { it.boundsInRoot.left }

        fun dragMonthUp() {
            val start = pickers()[1].boundsInRoot.center
            val distance = 45f
            scene.sendPointerEvent(PointerEventType.Press, start, type = PointerType.Touch, timeMillis = frame * 17)
            for (step in 1..12) {
                scene.sendPointerEvent(PointerEventType.Move, start - Offset(0f, distance * step / 12), type = PointerType.Touch, timeMillis = frame * 17)
                frames(2)
            }
            frames(20)
            scene.sendPointerEvent(PointerEventType.Release, start - Offset(0f, distance), type = PointerType.Touch, timeMillis = frame * 17)
            frames(100)
        }

        fun save(name: String) {
            scene.render(frame++ * 16_666_667L).use { image ->
                requireNotNull(image.encodeToData(EncodedImageFormat.PNG)).use { encoded ->
                    val output = File("build/reports/miuix-visual/$name.png")
                    output.parentFile.mkdirs()
                    output.writeBytes(encoded.bytes)
                }
            }
        }

        override fun close() = scene.close()
    }
}
