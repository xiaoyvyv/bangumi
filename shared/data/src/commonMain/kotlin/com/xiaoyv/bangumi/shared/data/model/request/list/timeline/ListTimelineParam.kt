package com.xiaoyv.bangumi.shared.data.model.request.list.timeline

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.core.types.list.ListTimelineType
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ListTimelineParam]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListTimelineParam(
    @SerialName("ui") val ui: PageUI = PageUI(),

    @field:ListTimelineType
    @SerialName("type")
    val type: Int = ListTimelineType.UNKNOWN,

    /**
     * [ListTimelineType.BROWSER] 浏览全部或好友时间线
     */
    @field:TimelineTarget
    val timlineMode: String = TimelineTarget.WHOLE,

    @field:TimelineCat
    val timelineCat: Int = TimelineCat.UNKNOWN,

    /**
     * [ListTimelineType.BROWSER_BY_WEB] 浏览全部或好友时间线
     */
    val browserWeb: ListTimelineWebParam = ListTimelineWebParam.Empty,

    /**
     * [ListTimelineType.USER] 用户的时间线
     */
    @SerialName("username")
    val username: String = "",
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListTimelineParam()
    }
}