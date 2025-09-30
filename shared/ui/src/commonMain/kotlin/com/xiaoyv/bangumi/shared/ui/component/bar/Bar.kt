package com.xiaoyv.bangumi.shared.ui.component.bar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_back
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalHideNavIcon
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.contentMargin
import org.jetbrains.compose.resources.stringResource


@Composable
fun BgmTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleContent: @Composable () -> Unit = {
        if (title != null) Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    onNavigationClick: () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = if (LocalHideNavIcon.current) null else {
        {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = BgmIconsMirrored.ArrowBack,
                    contentDescription = null
                )
            }
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = titleContent,
        modifier = modifier,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun BgmLargeTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String? = null,
    titleContent: @Composable () -> Unit = {
        if (title != null) {
            val progress = scrollBehavior?.state?.collapsedFraction ?: 0f
            Text(
                modifier = Modifier.padding(horizontal = (contentMargin - 16.dp) * (1 - progress)),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    },
    onNavigationClick: () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = {
        IconButton(onClick = onNavigationClick) {
            Icon(
                imageVector = BgmIconsMirrored.ArrowBack,
                contentDescription = stringResource(Res.string.global_back)
            )
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    collapsedHeight: Dp = TopAppBarDefaults.LargeAppBarCollapsedHeight,
    expandedHeight: Dp = TopAppBarDefaults.LargeAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
) {
    LargeTopAppBar(
        title = titleContent,
        modifier = modifier,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        collapsedHeight = collapsedHeight,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}