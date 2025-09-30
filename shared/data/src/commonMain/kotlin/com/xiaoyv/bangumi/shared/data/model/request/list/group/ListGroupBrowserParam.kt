package com.xiaoyv.bangumi.shared.data.model.request.list.group

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupFilterMode
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupSortType
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ListGroupBrowserParam(
    val mode: GroupFilterMode = GroupFilterMode.All,
    @field:GroupSortType val sort: String = GroupSortType.POSTS,
) {
    companion object {
        val Empty = ListGroupBrowserParam()
    }
}