package com.xiaoyv.bangumi.shared.data.model.request.list.index

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class IndexSearchBody(
    @SerialName("keyword") val keyword: String = "",
    @SerialName("exact") val exact: Boolean = false,
    @SerialName("order") val order: String = "updated_at",
) {
    companion object {
        val Empty = IndexSearchBody()
    }
}