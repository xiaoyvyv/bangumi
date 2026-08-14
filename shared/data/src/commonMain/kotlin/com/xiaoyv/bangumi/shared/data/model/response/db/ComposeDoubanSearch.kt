package com.xiaoyv.bangumi.shared.data.model.response.db

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeDoubanSearch(
    @SerialName("banned") val banned: String = "",
    @SerialName("types") val types: SerializeList<Type> = persistentListOf(),
    /** 当前 /search/subjects 响应的候选列表位于根节点。 */
    @SerialName("items") val items: SerializeList<ComposeDoubanSuggestCard> = persistentListOf(),
    @SerialName("show_more_subjects") val showMoreSubjects: Boolean = false,
    @SerialName("target_name") val targetName: String = "",
    @SerialName("total") val total: Int = 0,
    @SerialName("start") val start: Int = 0,
    @SerialName("count") val count: Int = 0,
    /** 兼容此前返回的 subjects.items 嵌套格式。 */
    @SerialName("subjects") val subjects: Subjects = Subjects(),
) {
    @Immutable
    @Serializable
    data class Type(
        @SerialName("type") val type: String = "",
        @SerialName("type_name") val typeName: String = "",
        @SerialName("total") val total: String = "",
        @SerialName("uuids") val uuids: SerializeList<String> = persistentListOf(),
    )

    @Immutable
    @Serializable
    data class Subjects(
        @SerialName("items") val items: SerializeList<ComposeDoubanSuggestCard> = persistentListOf(),
        @SerialName("show_more_subjects") val showMoreSubjects: Boolean = false,
        @SerialName("target_name") val targetName: String = "",
        @SerialName("total") val total: Int = 0,
        @SerialName("start") val start: Int = 0,
        @SerialName("count") val count: Int = 0,
    )
}
