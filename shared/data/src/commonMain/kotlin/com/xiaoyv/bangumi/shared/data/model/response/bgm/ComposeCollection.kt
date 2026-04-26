package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposeCollection]
 *
 * @since 2025/5/8
 */
@Immutable
@Serializable
data class ComposeCollection(
    @SerialName("comment") val comment: String = "",
    @SerialName("ep_status") val epStatus: Int = 0,
    @SerialName("vol_status") val volStatus: Int = 0,
    @SerialName("private") val isPrivate: Boolean = false,
    @SerialName("rate") val rate: Int = 0,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("subject_id") val subjectId: Long = 0,
    @SerialName("subject_type") @field:SubjectType val subjectType: Int = SubjectType.UNKNOWN,
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("type") @field:CollectionType val type: Int = CollectionType.UNKNOWN,
    @SerialName("updated_at") val updatedAt: String = "",
) {
    companion object {
        val Empty = ComposeCollection()
    }
}