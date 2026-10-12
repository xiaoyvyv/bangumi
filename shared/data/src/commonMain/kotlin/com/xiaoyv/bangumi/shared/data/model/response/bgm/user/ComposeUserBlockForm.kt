package com.xiaoyv.bangumi.shared.data.model.response.bgm.user

import androidx.compose.runtime.Immutable

/**
 * 网站隐私页面提供的绝交操作参数。
 *
 * @param formHash 当前页面的表单校验值。
 * @param unblockUrl 指定用户的同源取消绝交链接。
 */
@Immutable
data class ComposeUserBlockForm(
    val formHash: String,
    val unblockUrl: String?,
)
