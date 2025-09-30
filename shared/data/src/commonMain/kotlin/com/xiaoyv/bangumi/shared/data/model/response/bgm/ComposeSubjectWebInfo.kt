package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * [ComposeSubjectWebInfo]
 *
 * @since 2025/5/9
 */
@Immutable
@Serializable
data class ComposeSubjectWebInfo(
    /**
     * Html 部分的内容的详情，API 的infobox 没有ID，所以只能解析 Html 的详情展示
     */
    @SerialName("info") val info: String = "",
    @SerialName("shortInfo") val shortInfo: String = "",
    @Transient val shortInfoHtml: AnnotatedString = AnnotatedString(""),

    /**
     * Html 的推荐目录，API 暂无该功能
     */
    @SerialName("indexList") val indexList: SerializeList<ComposeIndex> = persistentListOf(),

    /**
     * 目录内解析复用
     */
    @SerialName("indexNote") val indexNote: String = "",
    @SerialName("indexRelatedId") val indexRelatedId: String = "",
) {
    companion object {
        val Empty = ComposeSubjectWebInfo()
    }
}
