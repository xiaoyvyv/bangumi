package com.xiaoyv.bangumi.shared.data

import com.xiaoyv.bangumi.shared.core.types.settings.SettingBottomBarAppearance
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 验证界面风格扩展前后的本地设置能够兼容读取和保存。
 */
class ComposeSettingCompatibilityTest {

    @Test
    fun legacySettingsKeepMaterialStyleAndExistingPreferences() {
        val legacyJson = """
            {
                "ui": {
                    "theme": 2,
                    "cacheState": false,
                    "trackingDesc": true,
                    "topAppBarScrollUp": false,
                    "timeMachineGridLimit": 25
                },
                "homeTab": {
                    "appearance": 1,
                    "defaultSelected": 3
                }
            }
        """.trimIndent()

        val settings = defaultJson.decodeFromString<ComposeSetting>(legacyJson)

        assertEquals(SettingUiStyle.MATERIAL3, settings.ui.style)
        assertFalse(settings.ui.monetTheme)
        assertEquals(ComposeSetting.UIConfig.DefaultThemeColor, settings.ui.themeColor)
        assertTrue(settings.ui.homeShortcutOrder.isEmpty())
        assertTrue(settings.ui.hiddenHomeShortcuts.isEmpty())
        assertEquals(SettingTheme.DARK, settings.ui.theme)
        assertFalse(settings.ui.cacheState)
        assertTrue(settings.ui.trackingDesc)
        assertFalse(settings.ui.topAppBarScrollUp)
        assertEquals(25, settings.ui.timeMachineGridLimit)
        assertEquals(SettingBottomBarAppearance.LIQUID_GLASS, settings.homeTab.appearance)
        assertEquals(3, settings.homeTab.defaultSelected)
    }

    @Test
    fun homeShortcutVisibilityAndOrderSurviveSaving() {
        val original = ComposeSetting(
            ui = ComposeSetting.UIConfig(
                homeShortcutOrder = listOf("pixiv", "rank", "newest"),
                hiddenHomeShortcuts = listOf("rank"),
            ),
        )
        assertEquals(original, defaultJson.decodeFromString<ComposeSetting>(defaultJson.encodeToString(original)))
    }

    @Test
    fun miuixMonetSettingsRoundTripWithUnsignedArgbColor() {
        val selectedColor = 0xFF105AA4L
        val original = ComposeSetting(
            ui = ComposeSetting.UIConfig(
                style = SettingUiStyle.MIUIX,
                theme = SettingTheme.LIGHT,
                themeColor = selectedColor,
                monetTheme = true,
                trackingDesc = true,
                cacheState = false,
            ),
            homeTab = ComposeSetting.HomeTabConfig(
                appearance = SettingBottomBarAppearance.LIQUID_GLASS,
                defaultSelected = 2,
            ),
        )

        val storedJson = defaultJson.encodeToString(original)
        val restored = defaultJson.decodeFromString<ComposeSetting>(storedJson)
        val storedUi = defaultJson.parseToJsonElement(storedJson).jsonObject.getValue("ui").jsonObject

        assertEquals(original, restored)
        assertEquals(selectedColor, storedUi.getValue("themeColor").jsonPrimitive.long)
        assertEquals(SettingUiStyle.MIUIX.toLong(), storedUi.getValue("style").jsonPrimitive.long)
        assertEquals("true", storedUi.getValue("monetTheme").jsonPrimitive.content)
    }
}
