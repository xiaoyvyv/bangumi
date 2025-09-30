@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.optImageUrl
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposeBlogDisplay]
 *
 * @since 2025/5/11
 */
@Immutable
@Serializable
data class ComposeBlogDisplay(
    @SerialName("id") val id: Long = 0,
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,
    @SerialName("entry") val blog: ComposeBlogEntry = ComposeBlogEntry.Empty,

    /**
     * 使用了API查询，下面内容待删除
     */
//    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("tag") val tags: SerializeList<String> = persistentListOf(),

    /**
     * 目录内解析复用
     */
    @SerialName("indexNote") val indexNote: String = "",
    @SerialName("indexRelatedId") val indexRelatedId: String = "",
) {
    val uniqueKey get() = if (blog != ComposeBlogEntry.Empty) blog.id else id
}


@Serializable
data class ComposeBlogEntry(
    @SerialName("createdAt") val createdAt: SerializeDateLong = 0,
    @SerialName("icon") val icon: String = "",
    @SerialName("id") val id: Long = 0,
    @SerialName("public") val `public`: Boolean = false,
    @SerialName("replies") val replies: Int = 0,
    @SerialName("summary") val summary: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("type") val type: Int = 0,
    @SerialName("uid") val uid: Long = 0,
    @SerialName("updatedAt") val updatedAt: SerializeDateLong = 0,

    /**
     * Blog detail
     */
    @SerialName("content") val content: String = "",
    @SerialName("noreply") val noreply: Int = 0,
    @SerialName("related") val related: Int = 0,
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,
    @SerialName("views") val views: Int = 0,
) {
    fun opt(): ComposeBlogEntry {
        if (icon.isBlank()) return this
        return copy(icon = icon.optImageUrl(mode = "l"))
    }

    companion object {
        val Empty = ComposeBlogEntry()

        fun List<ComposeBlogDisplay>.optImageUrl(): List<ComposeBlogDisplay> {
            return map {
                if (it.blog == Empty) it else it.copy(blog = it.blog.opt())
            }
        }
    }
}
