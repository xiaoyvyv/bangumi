@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.xiaoyv.bangumi.shared.core.utils.addUrl
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
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

    @SerialName("reactions") val reactions: SerializeList<ComposeReaction> = persistentListOf(),
    @SerialName("subjects") val subjects: SerializeList<ComposeSubject> = persistentListOf(),
) {

    @Composable
    fun rememberTimelineTitle(
        highlightColor: Color = MaterialTheme.colorScheme.primary,
        onBlogClickListener: (ComposeBlogEntry) -> Unit = { },
    ): AnnotatedString = remember(this, highlightColor, onBlogClickListener) {
        buildAnnotatedString {
            append("发表了新日志 ")
            addUrl(
                text = title,
                style = SpanStyle(
                    color = highlightColor,
                    textDecoration = TextDecoration.Underline
                ),
                listener = { onBlogClickListener(this@ComposeBlogEntry) }
            )
        }
    }

    fun opt(): ComposeBlogEntry {
        if (icon.isBlank()) return this
        return copy(icon = icon.sanitizeImageUrl())
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
