package com.xiaoyv.bangumi.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidBottomTab
import com.kyant.backdrop.catalog.components.LiquidBottomTabs
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeVectorTab
import kotlinx.collections.immutable.PersistentList
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigationSuiteScaffold1(
    modifier: Modifier,
    navigationSuiteItems: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    onTabSelected: (Int) -> Unit,
    selectedTabIndex: () -> Int,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        val backdrop = rememberLayerBackdrop()

        Box(
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(backdrop),
            content = content
        )

        LiquidBottomTabs(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            backdrop = backdrop,
            tabsCount = navigationSuiteItems.size,
        ) {
            repeat(navigationSuiteItems.size) { index ->
                LiquidBottomTab(onClick = { onTabSelected(index) }) {
                    Icon(
                        imageVector = navigationSuiteItems[index].second.icon,
                        contentDescription = stringResource(navigationSuiteItems[index].second.label),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    BasicText(
                        text = stringResource(navigationSuiteItems[index].second.label),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

