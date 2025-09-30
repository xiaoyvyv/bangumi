package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ComposeSubjectDisplay(
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("collection") val collection: ComposeCollection = ComposeCollection.Empty,
    @SerialName("positions") val positions: SerializeList<ComposePersonPosition> = persistentListOf(),

    /**
     * 查询条目的关联条目时填充数据
     */
    @SerialName("order") val order: Int = 0,
    @SerialName("relation") val relation: ComposeRelation = ComposeRelation.Empty,
) {
    companion object {
        val Empty = ComposeSubjectDisplay()
    }
}

@Immutable
@Serializable
data class ComposeRelation(
    @SerialName("cn") val cn: String = "",
    @SerialName("desc") val desc: String = "",
    @SerialName("en") val en: String = "",
    @SerialName("id") val id: Int = 0,
    @SerialName("jp") val jp: String = "",
) {
    val displayRelation get() = cn.ifBlank { en }.ifBlank { jp }

    companion object {
        val Empty = ComposeRelation()
    }
}