package com.xiaoyv.bangumi.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xiaoyv.bangumi.shared.core.types.settings.SettingBottomBarAppearance
import com.xiaoyv.bangumi.shared.ui.view.navigation.FloatingBottomBarDefaults
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalFloatingBottomBarActiveIndex
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalFloatingBottomBarIsDragging
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalMainBottomBarOverlap
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.view.navigation.FloatingBottomBar
import com.xiaoyv.bangumi.shared.ui.view.navigation.FloatingBottomBarItem
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalFloatingBottomBarContentColor
import com.xiaoyv.bangumi.shared.ui.view.navigation.floatingBottomBarBottomPadding
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeVectorTab
import com.xiaoyv.bangumi.shared.ui.kts.isWideScreen
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import kotlinx.collections.immutable.PersistentList
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.Badge as MiuixBadge
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationRail as MiuixNavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem as MiuixNavigationRailItem
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 主页面导航，主题风格与底栏外观独立设置，宽屏使用侧边导航。
 */
@Composable
fun BgmNavigationSuiteScaffold(
    @SettingBottomBarAppearance appearance: Int,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val miuix = isMiuixUi()

    if (isWideScreen) {
        if (miuix) {
            MiuixWideNavigation(modifier, tabs, selected, unreadBadge, onTabClick, content)
        } else {
            MaterialNavigation(modifier, tabs, selected, unreadBadge, onTabClick, content)
        }
        return
    }

    when {
        appearance == SettingBottomBarAppearance.LIQUID_GLASS && miuix -> MiuixLiquidNavigation(
            modifier = modifier,
            tabs = tabs,
            selected = selected,
            unreadBadge = unreadBadge,
            onTabClick = onTabClick,
            content = content,
        )

        appearance == SettingBottomBarAppearance.LIQUID_GLASS -> LiquidNavigation(
            modifier = modifier,
            tabs = tabs,
            selected = selected,
            unreadBadge = unreadBadge,
            onTabClick = onTabClick,
            content = content,
        )

        miuix -> MiuixNormalNavigation(
            modifier = modifier,
            tabs = tabs,
            selected = selected,
            unreadBadge = unreadBadge,
            onTabClick = onTabClick,
            content = content,
        )

        else -> MaterialNavigation(modifier, tabs, selected, unreadBadge, onTabClick, content)
    }
}

@Composable
private fun MaterialNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    val indicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val itemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(indicatorColor = indicatorColor),
    )
    val wide = isWideScreen
    val navigationContentMargin = ContentMargin

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            tabs.forEach { item ->
                val isSelected = selected == item.first
                item(
                    modifier = Modifier.padding(bottom = if (wide) navigationContentMargin else 0.dp),
                    label = {
                        Text(
                            text = stringResource(item.second.label),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = item.second.icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    selected = isSelected,
                    colors = itemColors,
                    badge = materialProfileBadge(item.first, unreadBadge),
                    onClick = { if (!isSelected) onTabClick(item.first) },
                )
            }
        },
        content = content,
    )
}

@Composable
private fun MiuixWideNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    Row(modifier = modifier.fillMaxSize()) {
        MiuixNavigationRail(modifier = Modifier.fillMaxHeight()) {
            tabs.forEach { item ->
                val isSelected = selected == item.first
                MiuixNavigationRailItem(
                    selected = isSelected,
                    onClick = { if (!isSelected) onTabClick(item.first) },
                    icon = item.second.icon,
                    label = stringResource(item.second.label),
                    badge = miuixProfileBadge(item.first, unreadBadge),
                )
            }
        }
        Box(modifier = Modifier.weight(1f).fillMaxSize()) { content() }
    }
}

@Composable
private fun MiuixNormalNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.weight(1f).fillMaxSize()
                .consumeWindowInsets(WindowInsets.navigationBars)
                .consumeWindowInsets(WindowInsets.captionBar),
        ) { content() }
        MiuixNavigationBar(showDivider = true) {
            tabs.forEach { item ->
                val isSelected = selected == item.first
                MiuixNavigationBarItem(
                    selected = isSelected,
                    onClick = { if (!isSelected) onTabClick(item.first) },
                    icon = item.second.icon,
                    label = stringResource(item.second.label),
                    badge = miuixProfileBadge(item.first, unreadBadge),
                )
            }
        }
    }
}

@Composable
private fun LiquidNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val barBottom = floatingBottomBarBottomPadding()
    val density = LocalDensity.current
    var barHeight by remember(density) { mutableStateOf(FloatingBottomBarDefaults.Height) }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(backdrop)
                .consumeWindowInsets(WindowInsets.navigationBars)
                .consumeWindowInsets(WindowInsets.captionBar),
        ) {
            CompositionLocalProvider(LocalMainBottomBarOverlap provides barHeight + barBottom + ContentMarginHalf) {
                content()
            }
        }
        FloatingBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = barBottom)
                .padding(horizontal = ContentMargin)
                .onSizeChanged { barHeight = with(density) { it.height.toDp() } },
            selectedIndex = { tabs.indexOfFirst { it.first == selected }.coerceAtLeast(0) },
            onSelected = { onTabClick(tabs[it].first) },
            backdrop = backdrop,
            tabsCount = tabs.size,
            colors = FloatingBottomBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                indicatorColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSurface,
                activeContentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            tabs.forEach { item ->
                FloatingBottomBarItem(
                    selected = item.first == selected,
                    onClick = { onTabClick(item.first) },
                    contentColor = if (item.first == selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ) {
                    LiquidTabContent(item, unreadBadge)
                }
            }
        }
    }
}

@Composable
private fun LiquidTabContent(
    item: Pair<Screen, ComposeVectorTab<String>>,
    unreadBadge: Int,
) {
    val label = stringResource(item.second.label)
    val showBadge = unreadBadge > 0 && item.first == Screen.Profile
    val tint = LocalFloatingBottomBarContentColor.current
        .takeIf { it != Color.Unspecified }
        ?: MaterialTheme.colorScheme.onSurface

    if (showBadge) {
        BadgedBox(badge = { Badge { Text(unreadBadge.toString()) } }) {
            Icon(item.second.icon, null, Modifier.size(26.dp), tint = tint)
        }
    } else {
        Icon(item.second.icon, null, Modifier.size(26.dp), tint = tint)
    }
    Text(text = label, color = tint, style = MaterialTheme.typography.bodySmall, maxLines = 1)
}

@Composable
private fun MiuixLiquidNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val barBottom = floatingBottomBarBottomPadding()
    val density = LocalDensity.current
    var barHeight by remember(density) { mutableStateOf(FloatingBottomBarDefaults.Height) }
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(backdrop)
                .consumeWindowInsets(WindowInsets.navigationBars)
                .consumeWindowInsets(WindowInsets.captionBar),
        ) {
            CompositionLocalProvider(LocalMainBottomBarOverlap provides barHeight + barBottom + ContentMarginHalf) {
                content()
            }
        }
        FloatingBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = barBottom)
                .padding(horizontal = ContentMargin)
                .onSizeChanged { barHeight = with(density) { it.height.toDp() } },
            selectedIndex = { tabs.indexOfFirst { it.first == selected }.coerceAtLeast(0) },
            onSelected = { onTabClick(tabs[it].first) },
            backdrop = backdrop,
            tabsCount = tabs.size,
        ) {
            tabs.forEachIndexed { index, item ->
                val activeIndex = LocalFloatingBottomBarActiveIndex.current
                val dragging = LocalFloatingBottomBarIsDragging.current
                val highlighted = if (dragging && activeIndex >= 0) {
                    activeIndex == index
                } else {
                    item.first == selected
                }
                val tint = if (highlighted) {
                    MiuixTheme.colorScheme.primary
                } else {
                    MiuixTheme.colorScheme.onSurface
                }
                FloatingBottomBarItem(
                    selected = item.first == selected,
                    onClick = { onTabClick(item.first) },
                    contentColor = tint,
                ) {
                    val label = stringResource(item.second.label)
                    val showBadge = unreadBadge > 0 && item.first == Screen.Profile
                    if (showBadge) {
                        top.yukonga.miuix.kmp.basic.BadgedBox(
                            badge = { MiuixBadge { MiuixText(unreadBadge.toString()) } },
                        ) {
                            MiuixIcon(item.second.icon, null, Modifier.size(26.dp), tint)
                        }
                    } else {
                        MiuixIcon(item.second.icon, null, Modifier.size(26.dp), tint)
                    }
                    MiuixText(text = label, color = tint, style = MiuixTheme.textStyles.footnote1, maxLines = 1)
                }
            }
        }
    }
}

private fun materialProfileBadge(screen: Screen, unreadBadge: Int): (@Composable () -> Unit)? {
    if (unreadBadge <= 0 || screen != Screen.Profile) return null
    return { Badge { Text(unreadBadge.toString()) } }
}

private fun miuixProfileBadge(screen: Screen, unreadBadge: Int): (@Composable () -> Unit)? {
    if (unreadBadge <= 0 || screen != Screen.Profile) return null
    return { MiuixBadge { MiuixText(unreadBadge.toString()) } }
}
