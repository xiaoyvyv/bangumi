package com.xiaoyv.bangumi.shared.data.model.response.bgm.index

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.xiaoyv.bangumi.shared.core.utils.addUrl
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeIndex(
    /**
     * Private api detail fields
     */
    @SerialName("award") val award: Int = 0,
    @SerialName("collects") val collects: Int = 0,
    @SerialName("created_at") val createdAt: SerializeDateLong = 0,
    @SerialName("updatedAt") val updatedAt: SerializeDateLong = 0,
    @SerialName("desc") val desc: String = "",
    @SerialName("id") val id: Long = 0,
    @SerialName("private") val `private`: Boolean = false,
    @SerialName("replies") val replies: Int = 0,
    @SerialName("stats") val stats: ComposeIndexStats = ComposeIndexStats(),
    @SerialName("title") val title: String = "",
    @SerialName("total") val total: Int = 0,
    @SerialName("type") val type: Int = 0,
    @SerialName("uid") val uid: Int = 0,
    @SerialName("user") val creator: ComposeUser = ComposeUser.Empty,
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

    @Composable
    fun rememberTimelineTitle(
        highlightColor: Color = MaterialTheme.colorScheme.primary,
        descStyle: SpanStyle = SpanStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
        ),
        onIndexClickListener: (ComposeIndex) -> Unit = {},
    ): AnnotatedString = remember(this, highlightColor, onIndexClickListener) {
        buildAnnotatedString {
            append("收藏了目录 ")
            addUrl(
                text = title,
                style = SpanStyle(
                    color = highlightColor,
                    textDecoration = TextDecoration.Underline
                ),
                listener = { onIndexClickListener(this@ComposeIndex) }
            )

            if (desc.isNotEmpty()) {
                appendLine()
                withStyle(descStyle) {
                    append(desc)
                }
            }
        }
    }

    companion object {
        val Empty = ComposeIndex()
    }
}

