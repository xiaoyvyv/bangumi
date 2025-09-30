package com.xiaoyv.bangumi.shared.data.model.request.list.mono

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MonoType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MonoCollectionBody(
    val username: String,

    /**
     * Mono 类型
     */
    @field:MonoType
    @SerialName("monoType")
    val monoType: Int = MonoType.UNKNOWN,
) {
    companion object {
        val Empty = MonoCollectionBody(username = "")
    }
}