package com.xiaoyv.bangumi.features.main

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeVectorTab
import kotlinx.collections.immutable.PersistentList

@Composable
fun NavigationSuiteScaffold1(
    modifier: Modifier,
    navigationSuiteItems: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    onTabSelected: (Int) -> Unit,
    selectedTabIndex: () -> Int,
    tabShape: Shape = CircleShape,
    content: @Composable BoxScope.() -> Unit
) {

}

