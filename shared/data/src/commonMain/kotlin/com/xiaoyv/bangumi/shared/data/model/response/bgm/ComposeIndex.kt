package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeIndex(
    @SerialName("ban") val ban: Boolean = false,
    @SerialName("created_at") val createdAt: SerializeDateLong = 0,
    @SerialName("creator") val creator: ComposeUser = ComposeUser.Empty,
    @SerialName("desc") val desc: String = "",
    @SerialName("id") val id: Long = 0,
    @SerialName("nsfw") val nsfw: Boolean = false,
    @SerialName("stat") val stat: Stat = Stat(),
    @SerialName("title") val title: String = "",
    @SerialName("total") val total: Int = 0,
    @SerialName("updated_at") val updatedAt: SerializeDateLong = 0,


    /**
     * Private api detail fields
     */
    @SerialName("award") val award: Int = 0,
    @SerialName("collects") val collects: Int = 0,
    @SerialName("replies") val replies: Int = 0,
    @SerialName("type") val type: Int = 0,
    @SerialName("uid") val uid: Long = 0,
    @SerialName("isBookmarked") val isBookmarked: Boolean = false,

    /**
     * Web解析填充，目录列表各个收录类型的数目映射，具体数据只有请求详情才填充
     */
    @SerialName("category") val category: SerializeMap<String, Int> = persistentMapOf(),
    @SerialName("subjects") val subjects: SerializeList<ComposeSubject> = persistentListOf(),
    @SerialName("topics") val topics: SerializeList<ComposeTopic> = persistentListOf(),
    @SerialName("eps") val eps: SerializeList<ComposeIndexEp> = persistentListOf(),
    @SerialName("blogs") val blogs: SerializeList<ComposeBlogDisplay> = persistentListOf(),
    @SerialName("monos") val monos: SerializeList<ComposeMonoDisplay> = persistentListOf(),
) {
    val shareUrl: String get() = WebConstant.URL_BASE_WEB + "index/" + id

    @Immutable
    @Serializable
    data class Stat(
        @SerialName("collects") val collects: Int = 0,
        @SerialName("comments") val comments: Int = 0,
    )

    companion object {
        val Empty = ComposeIndex()
    }
}