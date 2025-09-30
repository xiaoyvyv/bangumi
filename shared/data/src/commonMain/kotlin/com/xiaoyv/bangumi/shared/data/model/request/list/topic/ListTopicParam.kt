package com.xiaoyv.bangumi.shared.data.model.request.list.topic

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.list.ListTopicType
import com.xiaoyv.bangumi.shared.data.model.request.GroupTopicFilter
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val LocalListTopicParam = staticCompositionLocalOf { ListTopicParam.Empty }

/**
 * [ListTopicParam]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListTopicParam(
    @SerialName("ui") val ui: PageUI = PageUI(),

    @field:ListTopicType
    @SerialName("type")
    val type: Int = ListTopicType.UNKNOWN,

    /**
     * [ListTopicType.Companion.SUBJECT_TARGET]
     */
    @SerialName("subjectID") val subjectID: Long = 0,

    /**
     * [ListTopicType.Companion.GROUP_TARGET]
     */
    @SerialName("groupName") val groupName: String = "",

    /**
     * [ListTopicType.Companion.GROUP_ALL]
     */
    @SerialName("mode") @field:GroupTopicFilter val mode: String = "",

    /**
     * [ListTopicType.Companion.SEARCH]
     */
    @SerialName("search")
    val search: TopicSearchBody = TopicSearchBody.Empty,
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListTopicParam()
    }
}