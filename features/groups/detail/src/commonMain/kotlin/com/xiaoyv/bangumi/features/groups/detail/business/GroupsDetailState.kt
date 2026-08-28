package com.xiaoyv.bangumi.features.groups.detail.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [GroupsDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class GroupsDetailState(
    @SerialName("group") val group: ComposeGroup = ComposeGroup.Empty,
)
