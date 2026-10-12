package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalMainBottomBarOverlap
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalFloatingContentBottomPadding
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.FabPosition as MiuixFabPosition
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.Switch as MiuixSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 在两种设计风格间切换页面容器，并为悬浮底栏预留内容和操作区域。
 *
 * @param content 页面内容，接收系统边距；滚动列表的底部避让由内容边距处理。
 */
@Composable
fun BgmScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    val overlap = maxOf(LocalMainBottomBarOverlap.current, LocalFloatingContentBottomPadding.current)
    val layoutDirection = LocalLayoutDirection.current
    var hasFab by remember { mutableStateOf(false) }
    val hostedFab: @Composable () -> Unit = {
        RaisedScaffoldSlot(overlap, Modifier.onSizeChanged { hasFab = it.height > 0 }, floatingActionButton)
    }
    val hostedSnackbar: @Composable () -> Unit = {
        RaisedScaffoldSlot(if (hasFab) 0.dp else overlap, content = snackbarHost)
    }
    val hostedContent: @Composable (PaddingValues) -> Unit = { padding ->
        CompositionLocalProvider(
            LocalMainBottomBarOverlap provides 0.dp,
            LocalFloatingContentBottomPadding provides overlap,
        ) {
            content(
                PaddingValues(
                    start = padding.calculateStartPadding(layoutDirection),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(layoutDirection),
                    bottom = padding.calculateBottomPadding(),
                )
            )
        }
    }
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        if (isMiuixUi()) {
            MiuixScaffold(
                modifier = modifier,
                topBar = topBar,
                bottomBar = bottomBar,
                snackbarHost = hostedSnackbar,
                floatingActionButton = hostedFab,
                floatingActionButtonPosition = when (floatingActionButtonPosition) {
                    FabPosition.Start -> MiuixFabPosition.Start
                    FabPosition.Center -> MiuixFabPosition.Center
                    FabPosition.EndOverlay -> MiuixFabPosition.EndOverlay
                    else -> MiuixFabPosition.End
                },
                containerColor = containerColor,
                contentWindowInsets = contentWindowInsets,
                content = hostedContent,
            )
        } else {
            Scaffold(
                modifier = modifier,
                topBar = topBar,
                bottomBar = bottomBar,
                snackbarHost = hostedSnackbar,
                floatingActionButton = hostedFab,
                floatingActionButtonPosition = floatingActionButtonPosition,
                containerColor = containerColor,
                contentColor = contentColor,
                contentWindowInsets = contentWindowInsets,
                content = hostedContent,
            )
        }
    }
}

/**
 * 只在插槽实际有内容时增加底部避让，空 FAB 和空消息栏不产生占位。
 *
 * @param overlap 悬浮底栏覆盖的高度。
 * @param content 需要抬高的页面操作内容。
 */
@Composable
private fun RaisedScaffoldSlot(
    overlap: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val bottomPadding = overlap.roundToPx()
        val placeables = measurables.map { it.measure(constraints) }
        val width = placeables.maxOfOrNull { it.width } ?: 0
        val height = placeables.maxOfOrNull { it.height } ?: 0
        layout(width, if (height == 0) 0 else height + bottomPadding) {
            placeables.forEach { it.placeRelative(0, 0) }
        }
    }
}

/**
 * 保留现有列表插槽和语义颜色，使用当前设计风格的行布局。
 *
 * @param headlineContent 列表行的主标题。
 */
@Composable
fun BgmListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: (@Composable () -> Unit)? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
) {
    if (isMiuixUi()) {
        BasicComponent(
            modifier = if (colors.containerColor == Color.Unspecified) modifier else modifier.background(colors.containerColor),
            startAction = leadingContent?.let { action ->
                { CompositionLocalProvider(LocalContentColor provides colors.leadingIconColor) { action() } }
            },
            endActions = trailingContent?.let { action ->
                { CompositionLocalProvider(LocalContentColor provides colors.trailingIconColor) { action() } }
            },
        ) {
            if (overlineContent != null) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.overlineColor,
                    LocalTextStyle provides MiuixTheme.textStyles.footnote1,
                    content = overlineContent,
                )
            }
            CompositionLocalProvider(
                LocalContentColor provides colors.headlineColor,
                LocalTextStyle provides MiuixTheme.textStyles.headline1,
                content = headlineContent,
            )
            if (supportingContent != null) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.supportingTextColor,
                    LocalTextStyle provides MiuixTheme.textStyles.body2,
                    content = supportingContent,
                )
            }
        }
    } else {
        ListItem(
            headlineContent = headlineContent,
            modifier = modifier,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            colors = colors,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
        )
    }
}

/**
 * 使用当前设计风格的开关，受控值与原来的回调保持一致。
 *
 * @param checked 开关的受控状态。
 * @param onCheckedChange 用户请求切换状态时的回调。
 */
@Composable
fun BgmSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    if (isMiuixUi()) {
        MiuixSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            enabled = enabled,
        )
    } else {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier,
            thumbContent = thumbContent,
            enabled = enabled,
            colors = colors,
            interactionSource = interactionSource,
        )
    }
}
