package com.xiaoyv.bangumi.shared.data.model.request

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MagnetGardenSort
import com.xiaoyv.bangumi.shared.core.types.MagnetGardenTeam
import com.xiaoyv.bangumi.shared.core.types.MagnetGardenType
import kotlinx.serialization.Serializable

/**
 * https://share.dmhy.org/topics/list?keyword=%E8%8A%99%E8%8E%89%E8%8E%B2&sort_id=17&team_id=303&order=date-asc
 */
@Serializable
@Immutable
data class SearchMagnetBody(
    val keyword: String,
    @field:MagnetGardenType
    val typeId: Int = MagnetGardenType.TYPE_ALL,

    @field:MagnetGardenTeam
    val teamId: Int = MagnetGardenTeam.TEAM_ALL,

    /**
     * date-asc,date-desc,rel
     */
    @field:MagnetGardenSort
    val order: String = MagnetGardenSort.RELATED,
) {
    companion object {
        val Empty = SearchMagnetBody(keyword = "")
    }
}
