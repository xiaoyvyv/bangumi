package com.xiaoyv.bangumi.shared.data.model.request.list.index

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class IndexRelatedBody(
    val subjectId: Long = 0,
    val monoId: Long = 0,
) {
    companion object {
        val Empty = IndexRelatedBody()
    }
}