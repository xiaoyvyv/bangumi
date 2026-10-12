package com.xiaoyv.bangumi.shared.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.types.settings.SettingIndication
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.libnative.component.SideEffectForStatusBar
import org.koin.compose.KoinApplicationPreview
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.Colors
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

val BgmIcons = Icons.Rounded
val BgmDefaultIcons = Icons.Default
val BgmIconsMirrored = Icons.AutoMirrored.Rounded

val LocalBgmUiStyle = staticCompositionLocalOf { SettingUiStyle.MATERIAL3 }

/**
 * 当前界面是否使用 Miuix 组件。
 */
@Composable
@ReadOnlyComposable
fun isMiuixUi(): Boolean = LocalBgmUiStyle.current == SettingUiStyle.MIUIX

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun currentInDarkTheme(): Boolean {
    return when (currentSettings().ui.theme) {
        SettingTheme.SYSTEM -> isSystemInDarkTheme()
        SettingTheme.DARK -> true
        SettingTheme.LIGHT -> false
        else -> isSystemInDarkTheme()
    }
}


@Composable
fun BgmAppTheme(
    minWidthDp: Dp = 375.dp,
    darkTheme: Boolean = currentInDarkTheme(),
    modifier: Modifier = Modifier,
    containerColor: Color? = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit,
) = MinWidthDensityProvider(minWidthDp) {
    SideEffectForStatusBar(darkTheme)

    val settings = currentSettings()
    val uiStyle = settings.ui.style
    val useMiuix = uiStyle == SettingUiStyle.MIUIX
    val materialOverscrollFactory = LocalOverscrollFactory.current
    val baseMaterialScheme = if (darkTheme) darkScheme else lightScheme
    val miuixController = remember(darkTheme, settings.ui.monetTheme, settings.ui.themeColor) {
        ThemeController(
            colorSchemeMode = when {
                settings.ui.monetTheme && darkTheme -> ColorSchemeMode.MonetDark
                settings.ui.monetTheme -> ColorSchemeMode.MonetLight
                darkTheme -> ColorSchemeMode.Dark
                else -> ColorSchemeMode.Light
            },
            keyColor = settings.ui.themeColor.takeIf { settings.ui.monetTheme }?.let { Color(it.toInt()) },
            isDark = darkTheme,
        )
    }

    MiuixTheme(controller = miuixController) {
        val miuixIndication = LocalIndication.current
        val miuixOverscrollFactory = LocalOverscrollFactory.current
        val materialScheme = if (useMiuix || settings.ui.monetTheme) {
            baseMaterialScheme.withMiuixColors(MiuixTheme.colorScheme)
        } else {
            baseMaterialScheme
        }

        MaterialTheme(
            colorScheme = materialScheme,
            typography = if (useMiuix) miuixAppTypography() else rememberAppTypography(),
        ) {
            val materialIndication = LocalIndication.current
            val contentMargins = rememberContentMargins()

            CompositionLocalProvider(
                LocalBgmUiStyle provides uiStyle,
                LocalContentMargins provides contentMargins,
                LocalMinimumInteractiveComponentSize provides 20.dp,
                LocalContentColor provides materialScheme.onSurface,
                LocalOverscrollFactory provides if (useMiuix) miuixOverscrollFactory else materialOverscrollFactory,
                LocalIndication provides when (settings.ui.indication) {
                    SettingIndication.RIPPLE -> if (useMiuix) miuixIndication else materialIndication
                    SettingIndication.FADE -> DefaultIndication
                    else -> NoIndication
                },
            ) {
                Box(
                    modifier = if (containerColor == null) modifier else modifier.background(
                        if (containerColor == Color.Unspecified) materialScheme.background else containerColor
                    ),
                    content = content,
                )
            }
        }
    }
}

/**
 * 为仍使用 Material 的页面组件提供 Miuix 语义颜色。
 *
 * Miuix 的 secondary 用于控件轨道，其文字色不适合 Material 的强调色角色；
 * 这里用主色承担强调色，次级容器使用 Miuix 次级按钮的成对颜色。
 *
 * @param colors 当前 Miuix 配色。
 */
private fun ColorScheme.withMiuixColors(colors: Colors): ColorScheme = copy(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryContainer,
    onPrimaryContainer = colors.onPrimaryContainer,
    secondary = colors.primary,
    onSecondary = colors.onPrimary,
    secondaryContainer = colors.secondaryVariant,
    onSecondaryContainer = colors.onSecondaryVariant,
    tertiaryContainer = colors.tertiaryContainer,
    onTertiaryContainer = colors.onTertiaryContainer,
    background = colors.background,
    onBackground = colors.onBackground,
    surface = colors.surface,
    onSurface = colors.onSurface,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.onSurfaceVariantSummary,
    surfaceTint = colors.primary,
    inversePrimary = colors.primary,
    outline = colors.outline,
    outlineVariant = colors.dividerLine,
    error = colors.error,
    onError = colors.onError,
    errorContainer = colors.errorContainer,
    onErrorContainer = colors.onErrorContainer,
    surfaceDim = colors.surface,
    surfaceBright = colors.surfaceVariant,
    surfaceContainerLowest = colors.surfaceVariant,
    surfaceContainerLow = colors.surfaceContainer,
    surfaceContainer = colors.surfaceContainer,
    surfaceContainerHigh = colors.surfaceContainerHigh,
    surfaceContainerHighest = colors.surfaceContainerHighest,
)

/**
 * 某些设备最小宽度逻辑单位会被修改，这里强制恢复
 */
@Composable
fun MinWidthDensityProvider(
    minWidthDp: Dp,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints {
        val currentDensity = LocalDensity.current
        val currentWidthDp = maxWidth
        if (currentWidthDp < minWidthDp) {
            val scale = currentWidthDp.value / minWidthDp.value
            val targetDensity = Density(
                density = currentDensity.density * scale,
                fontScale = currentDensity.fontScale,
            )

            CompositionLocalProvider(LocalDensity provides targetDensity) {
                content()
            }
        } else {
            content()
        }
    }
}

@Composable
fun PreviewColumn(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    module: ModuleDeclaration = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    KoinApplicationPreview(application = { module(moduleDeclaration = module) }) {
        BgmAppTheme(
            minWidthDp = 375.dp,
            darkTheme = darkTheme,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier, content = content)
        }
    }
}
