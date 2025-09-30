package com.xiaoyv.bangumi.features.mikan.studio.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanGroup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [MikanStudioState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class MikanStudioState(
    @SerialName("mikanId") val mikanId: String,
    @SerialName("groupInfo") val groupInfo: List<ComposeMikanGroup> = emptyList(),
)
