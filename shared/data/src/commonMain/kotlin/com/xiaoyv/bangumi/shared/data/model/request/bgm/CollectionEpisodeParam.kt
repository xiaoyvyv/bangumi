package com.xiaoyv.bangumi.shared.data.model.request.bgm

import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param episodeId
 * @param type
 */
@Serializable
data class CollectionEpisodeParam(
    @SerialName(value = "episode_id") val episodeId: List<Long>? = null,
    @SerialName(value = "type") @field:CollectionEpisodeType val type: Int,
)

