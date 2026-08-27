package com.xiaoyv.bangumi.shared.data.model.request.list.topic

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.topic_list_all
import com.xiaoyv.bangumi.core_resource.resources.topic_list_joined
import com.xiaoyv.bangumi.core_resource.resources.topic_list_replied
import com.xiaoyv.bangumi.core_resource.resources.topic_list_title
import com.xiaoyv.bangumi.shared.core.types.list.ListTopicType
import com.xiaoyv.bangumi.shared.data.model.request.bgm.GroupTopicFilter
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

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

    val title: StringResource
        get() = when (type) {
            ListTopicType.GROUP_ALL -> when (mode) {
                GroupTopicFilter.ALL -> Res.string.topic_list_all
                GroupTopicFilter.JOINED -> Res.string.topic_list_joined
                GroupTopicFilter.CREATED -> Res.string.topic_list_joined
                GroupTopicFilter.REPLIED -> Res.string.topic_list_replied
                else -> Res.string.topic_list_title
            }

            else -> Res.string.topic_list_title
        }

    companion object {
        val Empty = ListTopicParam()
    }
}