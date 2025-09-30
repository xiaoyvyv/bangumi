package com.xiaoyv.bangumi.shared.data.model.request.list.mono

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class MonoSearchBody(
    @SerialName("filter") val filter: MonoSearchFilter = MonoSearchFilter.Empty,
    @SerialName("keyword") val keyword: String = "",
) {
    companion object {
        val Empty = MonoSearchBody()
    }
}

@Serializable
data class MonoSearchFilter(
    /**
     * Only for person
     */
    @SerialName("career") val career: SerializeList<String> = persistentListOf(),

    /**
     * Only for character
     */
    @SerialName("nsfw") val nsfw: Boolean = true,
) {
    companion object {
        val Empty = MonoSearchFilter()
    }
}

