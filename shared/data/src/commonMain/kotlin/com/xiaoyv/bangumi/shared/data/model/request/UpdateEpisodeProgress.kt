package com.xiaoyv.bangumi.shared.data.model.request

import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param batch 是否批量更新(看到当前章节), 批量更新时 type 无效
 * @param type
 */
@Serializable
data class UpdateEpisodeProgress(
    /* 是否批量更新(看到当前章节), 批量更新时 type 无效 */
    @SerialName(value = "batch") val batch: Boolean? = null,
    @SerialName(value = "type") @field:CollectionEpisodeType val type: Int? = null,
)

