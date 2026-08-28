package com.xiaoyv.bangumi.shared.data.model.request.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Immutable
data class IndexCreateParam(
    @SerialName(value = "cat") @IndexCatType val cat: Int,
    @SerialName(value = "sid") val sid: Long,
    @SerialName(value = "order") val order: Int? = null,
    @SerialName(value = "comment") val comment: String? = null,
    @SerialName(value = "award") val award: String? = null,

    /**
     * Local fields
     */
    @Transient val displayName: String = "",
) {
    val uniqueKey = "$cat-$sid"
}