package com.xiaoyv.bangumi.shared.ui.component.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_back
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalHideNavIcon
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.ui.component.scroll.LocalScrollUpState
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import com.xiaoyv.bangumi.shared.ui.view.MiuixCustomTitleBar
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import top.yukonga.miuix.kmp.basic.SmallTopAppBar as MiuixSmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar
import top.yukonga.miuix.kmp.theme.LocalContentColor as MiuixLocalContentColor
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@Composable
fun BgmTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleContent: @Composable BoxScope.() -> Unit = {
        if (title != null && !isMiuixUi()) Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    onNavigationClick: () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = if (LocalHideNavIcon.current) null else {
        {
            BgmNavigationIcon(onNavigationClick)
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val scrollTopState = LocalScrollUpState.current
    val scrollUpEnabled = currentSettings().ui.topAppBarScrollUp
    val scope = rememberCoroutineScope()

    if (isMiuixUi()) {
        SideEffect {
            scrollBehavior?.state?.let {
                it.heightOffsetLimit = 0f
                it.heightOffset = 0f
            }
        }
        if (title == null) {
            MiuixCustomTitleBar(
                modifier = modifier.background(colors.containerColor).windowInsetsPadding(windowInsets),
                titleContent = titleContent,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                onTitleClick = { if (scrollUpEnabled) scope.launch { scrollTopState.scrollTop() } },
            )
            return
        }
        MiuixSmallTopAppBar(
            title = title.orEmpty(),
            modifier = modifier.background(colors.containerColor).windowInsetsPadding(windowInsets).clickWithoutRipped {
                if (scrollUpEnabled) scope.launch { scrollTopState.scrollTop() }
            },
            color = colors.containerColor,
            titleColor = colors.titleContentColor,
            navigationIcon = {
                CompositionLocalProvider(
                    LocalContentColor provides colors.navigationIconContentColor,
                    MiuixLocalContentColor provides colors.navigationIconContentColor,
                ) { navigationIcon?.invoke() }
            },
            actions = {
                CompositionLocalProvider(
                    LocalContentColor provides colors.actionIconContentColor,
                    MiuixLocalContentColor provides colors.actionIconContentColor,
                ) { actions() }
            },
            defaultWindowInsetsPadding = false,
        )
        return
    }

    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickWithoutRipped {
                        if (scrollUpEnabled) scope.launch { scrollTopState.scrollTop() }
                    },
                content = titleContent
            )
        },
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
    titleContent: @Composable BoxScope.() -> Unit = {
        if (title != null && !isMiuixUi()) {
            val progress = scrollBehavior?.state?.collapsedFraction ?: 0f
            Text(
                modifier = Modifier.padding(horizontal = (ContentMargin - 16.dp) * (1 - progress)),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    },
    onNavigationClick: () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = {
        BgmNavigationIcon(onNavigationClick)
    },
    actions: @Composable RowScope.() -> Unit = {},
    collapsedHeight: Dp = TopAppBarDefaults.LargeAppBarCollapsedHeight,
    expandedHeight: Dp = TopAppBarDefaults.LargeAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {
    val scrollTopState = LocalScrollUpState.current
    val scrollUpEnabled = currentSettings().ui.topAppBarScrollUp
    val scope = rememberCoroutineScope()

    if (isMiuixUi()) {
        if (title == null) {
            MiuixCustomTitleBar(
                modifier = modifier.background(colors.containerColor).windowInsetsPadding(windowInsets),
                titleContent = titleContent,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                onTitleClick = { if (scrollUpEnabled) scope.launch { scrollTopState.scrollTop() } },
            )
            return
        }
        val miuixBehavior = MiuixScrollBehavior()
        LaunchedEffect(scrollBehavior, miuixBehavior) {
            if (scrollBehavior != null) {
                snapshotFlow { scrollBehavior.state.heightOffset }.collect {
                    miuixBehavior.state.heightOffset = it
                }
            }
        }
        MiuixTopAppBar(
            title = title.orEmpty(),
            modifier = modifier.background(colors.containerColor).windowInsetsPadding(windowInsets).clickWithoutRipped {
                if (scrollUpEnabled) scope.launch { scrollTopState.scrollTop() }
            }.onSizeChanged {
                val limit = miuixBehavior.state.heightOffsetLimit
                if (limit != -Float.MAX_VALUE) scrollBehavior?.state?.let { state ->
                    state.heightOffsetLimit = limit
                    state.heightOffset = state.heightOffset.coerceIn(limit, 0f)
                }
            },
            color = colors.containerColor,
            titleColor = colors.titleContentColor,
            largeTitleColor = colors.titleContentColor,
            navigationIcon = {
                CompositionLocalProvider(
                    LocalContentColor provides colors.navigationIconContentColor,
                    MiuixLocalContentColor provides colors.navigationIconContentColor,
                ) { navigationIcon?.invoke() }
            },
            actions = {
                CompositionLocalProvider(
                    LocalContentColor provides colors.actionIconContentColor,
                    MiuixLocalContentColor provides colors.actionIconContentColor,
                ) { actions() }
            },
            scrollBehavior = miuixBehavior,
            defaultWindowInsetsPadding = false,
        )
        return
    }

    LargeTopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickWithoutRipped {
                        if (scrollUpEnabled) scope.launch { scrollTopState.scrollTop() }
                    },
                content = titleContent
            )
        },
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

/**
 * 统一返回操作的外观和辅助阅读标签。
 *
 * @param onClick 返回操作。
 */
@Composable
private fun BgmNavigationIcon(onClick: () -> Unit) {
    if (isMiuixUi()) {
        MiuixIconButton(onClick = onClick) {
            MiuixIcon(
                imageVector = BgmIconsMirrored.ArrowBack,
                contentDescription = stringResource(Res.string.global_back),
            )
        }
    } else {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = BgmIconsMirrored.ArrowBack,
                contentDescription = stringResource(Res.string.global_back),
            )
        }
    }
}
