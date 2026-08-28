package com.xiaoyv.bangumi.shared.data.model.request.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ClearNoticeRequest(
    @SerialName(value = "id")
    val id: SerializeList<Long>? = null
)