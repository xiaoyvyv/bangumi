package com.xiaoyv.bangumi.shared.data.model.request.list.timeline

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ListTimelineWebParam(
    @field:TimelineTarget val target: String = TimelineTarget.WHOLE,
    @field:TimelineTab val type: String = TimelineTab.DYNAMIC,
    val username: String = "",
) {
    companion object {
        val Empty = ListTimelineWebParam(TimelineTarget.WHOLE, TimelineTab.DYNAMIC)
    }
}