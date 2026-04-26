package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * [ComposeMonoWebInfo]
 *
 * @since 2025/5/18
 */
@Immutable
@Serializable
data class ComposeMonoWebInfo(
    @SerialName("info") val info: String = "",
    @SerialName("shortInfo") val shortInfo: String = "",
    @Transient val shortInfoHtml: AnnotatedString = AnnotatedString(""),

    @SerialName("indexList") val indexList: SerializeList<ComposeIndex> = persistentListOf(),

    @SerialName("comments") val comments: SerializeList<ComposeComment> = persistentListOf(),
) {

    /**
     * 恢复 Html 渲染内容
     */
    fun restore(): ComposeMonoWebInfo {
        return copy(
            shortInfoHtml = shortInfo.parseAsHtml(),
            comments = comments.map { it.copy(commentHtml = it.comment.parseAsHtml()) }.toPersistentList()
        )
    }

    companion object Companion {
        val Empty: ComposeMonoWebInfo = ComposeMonoWebInfo()
    }
}
