package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import top.yukonga.miuix.kmp.basic.TopAppBarDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.LocalContentColor as MiuixLocalContentColor

/**
 * 为图标、搜索词和会话信息等自定义标题保留单行导航布局。
 *
 * Miuix 公共标题栏只接受字符串，这里使用基础布局承载现有标题插槽。
 *
 * @param titleContent 调用方提供的主标题内容。
 * @param onTitleClick 点击主标题时的操作。
 */
@Composable
internal fun MiuixCustomTitleBar(
    modifier: Modifier,
    titleContent: @Composable BoxScope.() -> Unit,
    navigationIcon: (@Composable () -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    colors: TopAppBarColors,
    onTitleClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.containerColor)
            .heightIn(min = TopAppBarDefaults.CollapsedHeight)
            .padding(horizontal = ContentMargin),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides colors.navigationIconContentColor,
            MiuixLocalContentColor provides colors.navigationIconContentColor,
        ) { navigationIcon?.invoke() }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = ContentMarginHalf)
                .clickWithoutRipped(onTitleClick),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides colors.titleContentColor,
                MiuixLocalContentColor provides colors.titleContentColor,
                LocalTextStyle provides MiuixTheme.textStyles.title4,
            ) { titleContent() }
        }
        CompositionLocalProvider(
            LocalContentColor provides colors.actionIconContentColor,
            MiuixLocalContentColor provides colors.actionIconContentColor,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, content = actions)
        }
    }
}
