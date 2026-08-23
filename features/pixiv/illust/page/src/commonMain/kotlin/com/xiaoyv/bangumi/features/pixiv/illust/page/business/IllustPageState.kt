package com.xiaoyv.bangumi.features.pixiv.illust.page.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.request.list.pixiv.ListIllustParam

/**
 * [IllustPageState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class IllustPageState(
    val param: ListIllustParam = ListIllustParam.Empty,
)
