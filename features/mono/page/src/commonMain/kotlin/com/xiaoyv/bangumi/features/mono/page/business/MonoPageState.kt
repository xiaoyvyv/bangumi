package com.xiaoyv.bangumi.features.mono.page.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam

/**
 * [MonoPageState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class MonoPageState(
    val param: ListMonoParam = ListMonoParam.Empty,
)
