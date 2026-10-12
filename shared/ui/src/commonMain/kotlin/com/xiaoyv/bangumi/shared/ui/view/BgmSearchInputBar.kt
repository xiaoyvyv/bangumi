package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.SearchBar

/**
 * 搜索页使用原生 Miuix 搜索栏，查询和提交仍由页面状态持有。
 *
 * @param query 当前查询文本。
 * @param label 输入提示。
 * @param searchIcon 提交搜索图标。
 * @param backIcon 返回图标。
 * @param backLabel 返回操作的无障碍描述。
 * @param clearLabel 清空输入的无障碍描述。
 * @param clearIcon 清空输入图标。
 * @param onQueryChange 输入内容变更回调。
 * @param onSearch 提交搜索回调。
 * @param onBack 返回搜索页的上一页。
 */
@Composable
fun BgmSearchInputBar(
    query: String,
    label: String,
    searchIcon: ImageVector,
    backIcon: ImageVector,
    backLabel: String,
    clearLabel: String,
    clearIcon: ImageVector,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        modifier = modifier.fillMaxWidth(),
        expanded = false,
        onExpandedChange = {},
        inputField = {
            InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = { onSearch() },
                label = label,
                expanded = true,
                onExpandedChange = {},
                leadingIcon = {
                    IconButton(onClick = onBack) {
                        Icon(backIcon, contentDescription = backLabel)
                    }
                },
                trailingIcon = {
                    Row(Modifier.padding(end = ContentMarginHalf)) {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(clearIcon, contentDescription = clearLabel)
                            }
                        }
                        IconButton(onClick = onSearch) {
                            Icon(searchIcon, contentDescription = label)
                        }
                    }
                },
            )
        },
    ) {}
}
