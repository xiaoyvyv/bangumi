package com.xiaoyv.bangumi.shared.data.model.request

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class IndexTarget(
    @SerialName("cat") @field:IndexCatType val cat: Int,
    @SerialName("displayName") val displayName: String,
    @SerialName("relateId") val relateId: Long,
) {
    val uniqueKey = buildString { append(cat).append(relateId).append(displayName) }
}