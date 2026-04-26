package com.xiaoyv.bangumi.shared.data.model.response.bgm.index

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposeIndexEp]
 *
 * @since 2025/5/8
 */
@Immutable
@Serializable
data class ComposeIndexEp(
    @SerialName("id") val id: Long = 0,
    @SerialName("name") val name: String = "",
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Companion.Empty,

    /**
     * 目录内解析复用
     */
    @SerialName("indexNote") val indexNote: String = "",
    @SerialName("indexRelatedId") val indexRelatedId: String = "",
) {
    companion object Companion {
        val Empty = ComposeIndexEp()
    }
}