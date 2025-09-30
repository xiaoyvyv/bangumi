package com.xiaoyv.bangumi.features.tag.detail.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.SubjectType

/**
 * [TagDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TagDetailState(
    @field:SubjectType val type: Int = SubjectType.ANIME,
)
