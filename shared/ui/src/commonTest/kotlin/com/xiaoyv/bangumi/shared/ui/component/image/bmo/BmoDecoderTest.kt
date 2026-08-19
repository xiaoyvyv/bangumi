package com.xiaoyv.bangumi.shared.ui.component.image.bmo

import com.xiaoyv.bangumi.shared.core.bmo.BmoDecoder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BmoDecoderTest {

    @Test
    fun testDecodeStandardBmoCode() {
        val code = "(bmo_f1_m2_e3)"
        val result = BmoDecoder.decode(code)

        assertEquals("bmo_f1_m2_e3", result.rawCode)
        assertEquals(3, result.items.size)

        // 校验图层排序：face (layer 1) -> mouth (layer 2) -> eyes (layer 3)
        assertEquals("1", result.items[0].id)
        assertEquals("face", result.items[0].category)
        assertEquals(1, result.items[0].layer)

        assertEquals("2", result.items[1].id)
        assertEquals("mouth", result.items[1].category)
        assertEquals(2, result.items[1].layer)

        assertEquals("3", result.items[2].id)
        assertEquals("eyes", result.items[2].category)
        assertEquals(3, result.items[2].layer)
    }

    @Test
    fun testDecodeModifiers() {
        val code = "(bmo_f1:h=20|l=-10|tf=5_m2:rotate=90)"
        val result = BmoDecoder.decode(code)

        assertEquals(2, result.items.size)

        val faceItem = result.items.first { it.category == "face" }
        assertEquals(20f, faceItem.modifiers.hue)
        assertEquals(-10f, faceItem.modifiers.lightness)
        assertTrue(faceItem.modifiers.flipH)
        assertEquals(90f, faceItem.modifiers.rotation)

        val mouthItem = result.items.first { it.category == "mouth" }
        assertEquals(90f, mouthItem.modifiers.rotation)
    }

    @Test
    fun testDecodeCompactBmoCode() {
        val code = "(bmoCAwUogQYE)"
        val result = BmoDecoder.decode(code)

        assertEquals("bmoCAwUogQYE", result.rawCode)
        assertEquals(2, result.items.size)

        val faceItem = result.items.first { it.category == "face" }
        assertEquals("1", faceItem.id)
        assertEquals(1, faceItem.layer)
        assertEquals(20f, faceItem.modifiers.hue)
        assertTrue(faceItem.modifiers.flipH)
        assertEquals(90f, faceItem.modifiers.rotation)

        val mouthItem = result.items.first { it.category == "mouth" }
        assertEquals("2", mouthItem.id)
        assertEquals(2, mouthItem.layer)
        assertEquals(90f, mouthItem.modifiers.rotation)
    }

    @Test
    fun testDecodeUserCompactBmoCode() {
        val code = "(bmoCgAKAEIAdgDaAO4A-gEOAUoBW)"
        val result = BmoDecoder.decode(code)

        assertEquals("bmoCgAKAEIAdgDaAO4A-gEOAUoBW", result.rawCode)
        assertEquals(9, result.items.size)

        // 校验 9 个图层及其顺序
        assertEquals("face", result.items[0].category)
        assertEquals("3", result.items[0].id)
        assertEquals(1, result.items[0].layer)

        assertEquals("mouth", result.items[1].category)
        assertEquals("12", result.items[1].id)
        assertEquals(2, result.items[1].layer)

        assertEquals("eyes", result.items[2].category)
        assertEquals("2", result.items[2].id)
        assertEquals(3, result.items[2].layer)

        // 配饰图层 (layer 4)
        val accItems = result.items.filter { it.category == "accessories" }
        assertEquals(4, accItems.size)
        assertEquals(listOf("2", "7", "10", "15"), accItems.map { it.id })

        // 其他图层 (layer 4)
        val otherItems = result.items.filter { it.category == "others" }
        assertEquals(2, otherItems.size)
        assertEquals(listOf("9", "13"), otherItems.map { it.id })
    }

    @Test
    fun testCategoryMaxSelectLimit() {
        val code = "(bmo_f1_f2_m1)"
        val result = BmoDecoder.decode(code)

        val faceItems = result.items.filter { it.category == "face" }
        assertEquals(1, faceItems.size)
        assertEquals("1", faceItems[0].id)
    }
}
