package com.xiaoyv.bangumi.features.subject.page.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam

/**
 * [SubjectPageState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class SubjectPageState(
    val param: ListSubjectParam,
)
