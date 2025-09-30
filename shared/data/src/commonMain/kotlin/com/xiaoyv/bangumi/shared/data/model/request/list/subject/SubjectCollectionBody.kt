package com.xiaoyv.bangumi.shared.data.model.request.list.subject

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CollectionWebSortType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SubjectCollectionBody(
    val username: String,

    @field:SubjectType
    @SerialName("subjectType")
    val subjectType: Int,

    @field:CollectionType
    @SerialName("collectionType")
    val collectionType: Int,

    @field:CollectionWebSortType
    @SerialName("collectionSort")
    val collectionSort: String = CollectionWebSortType.RATE,
) {
    companion object {
        val Empty = SubjectCollectionBody(
            username = "",
            subjectType = SubjectType.UNKNOWN,
            collectionType = CollectionType.UNKNOWN
        )
    }
}