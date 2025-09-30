package com.xiaoyv.bangumi.features.mikan.detail.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [MikanDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class MikanDetailState(
    @SerialName("groupName") val groupName: String,
    @SerialName("resources") val resources: List<ComposeMikanResource> = emptyList(),
    @SerialName("checkMode") val checkMode: Boolean = false,
    @SerialName("checkItems") val checkItems: List<Int> = emptyList(),
)
