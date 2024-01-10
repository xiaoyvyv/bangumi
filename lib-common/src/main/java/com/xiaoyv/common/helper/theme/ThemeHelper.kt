@file:SuppressLint("RestrictedApi")

package com.xiaoyv.common.helper.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.os.Build
import com.google.android.material.color.ColorResourcesOverride
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.MaterialColorUtilitiesHelper
import com.google.android.material.color.ThemeUtils
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.SchemeContent
import com.google.android.material.resources.MaterialAttributes
import com.xiaoyv.common.helper.callback.ThemeActivityLifecycleCallback
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.getSystemServiceCompat

/**
 * Class: [ThemeHelper]
 *
 * @author why
 * @since 1/10/24
 */
class ThemeHelper private constructor() {
    private val themeActivityLifecycleCallback by lazy { ThemeActivityLifecycleCallback() }

    /**
     * 动态主题适配
     */
    fun initTheme(app: Application) {
        app.registerActivityLifecycleCallbacks(themeActivityLifecycleCallback)
    }

    fun applyToActivityIfAvailable(activity: Activity, dynamicColorsOptions: DynamicColorsOptions) {
        if (DynamicColors.isDynamicColorAvailable()) {
            var theme = 0
            val basedSeedColor = dynamicColorsOptions.contentBasedSeedColor

            if (basedSeedColor == null) {
                theme = if (dynamicColorsOptions.themeOverlay == 0) {
                    getDefaultThemeOverlay(
                        activity,
                        intArrayOf(GoogleAttr.dynamicColorThemeOverlay)
                    )
                } else {
                    dynamicColorsOptions.themeOverlay
                }
            }

            if (dynamicColorsOptions.precondition.shouldApplyDynamicColors(activity, theme)) {
                if (basedSeedColor != null) {
                    val scheme = SchemeContent(
                        Hct.fromInt(basedSeedColor),
                        !isLightTheme(activity),
                        getSystemContrast(activity)
                    )
                    val resourcesOverride = ColorResourcesOverride.getInstance() ?: return
                    if (!resourcesOverride.applyIfPossible(
                            activity,
                            MaterialColorUtilitiesHelper.createColorResourcesIdsToColorValues(scheme)
                        )
                    ) {
                        return
                    }
                } else {
                    ThemeUtils.applyThemeOverlay(activity, theme)
                }

                dynamicColorsOptions.onAppliedCallback.onApplied(activity)
            }
        }
    }

    /**
     * 重构全部页面
     */
    fun recreateAllActivity() {
        themeActivityLifecycleCallback.recreateAll()
    }

    private fun getDefaultThemeOverlay(context: Context, themeOverlayAttribute: IntArray): Int {
        val dynamicColorAttributes = context.obtainStyledAttributes(themeOverlayAttribute)
        val theme = dynamicColorAttributes.getResourceId(0, 0)
        dynamicColorAttributes.recycle()
        return theme
    }

    private fun isLightTheme(context: Context): Boolean {
        return MaterialAttributes.resolveBoolean(context, GoogleAttr.isLightTheme, true)
    }

    private fun getSystemContrast(context: Context): Double {
        return if (Build.VERSION.SDK_INT >= 34) context.getSystemServiceCompat(UiModeManager::class.java).contrast.toDouble() else 0.0
    }

    companion object {

        val instance by lazy { ThemeHelper() }
    }
}