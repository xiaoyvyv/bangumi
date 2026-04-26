package com.xiaoyv.bangumi.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveComponentOverrideApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationItemIcon
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldOverride
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldOverrideScope
import androidx.compose.material3.adaptive.navigationsuite.rememberStateOfItems
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidBottomTab
import com.kyant.backdrop.catalog.components.LiquidBottomTabs
import com.xiaoyv.bangumi.features.main.DefaultNavigationSuiteScaffoldOverride.NavigationSuiteScaffold

private val NoWindowInsets = WindowInsets(0, 0, 0, 0)

/**
 * This override provides the default behavior of the [NavigationSuiteScaffold] component.
 *
 * [NavigationSuiteScaffoldOverride] used when no override is specified.
 */
@ExperimentalMaterial3AdaptiveComponentOverrideApi
object DefaultNavigationSuiteScaffoldOverride : NavigationSuiteScaffoldOverride {
    @Composable
    override fun NavigationSuiteScaffoldOverrideScope.NavigationSuiteScaffold() {
        Surface(color = containerColor, contentColor = contentColor) {
            Box(modifier = modifier) {
                val backdrop = rememberLayerBackdrop()
                val scope by rememberStateOfItems(navigationSuiteItems)

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .layerBackdrop(backdrop),
                    content = { content() }
                )

                LiquidBottomTabs(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp),
                    selectedTabIndex = { scope.itemList.indexOfFirst { it.selected } },
                    onTabSelected = { scope.itemList[it].onClick.invoke() },
                    backdrop = backdrop,
                    tabsCount = scope.itemsCount,
                ) {
                    scope.itemList.forEach {
                        LiquidBottomTab(
                            modifier = it.modifier,
                            onClick = it.onClick,
                        ) {
                            NavigationItemIcon(icon = it.icon, badge = it.badge)

                            ProvideContentColorTextStyle(
                                contentColor = contentColor,
                                textStyle = MaterialTheme.typography.bodySmall,
                                content = {
                                    it.label?.invoke()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content,
    )
}