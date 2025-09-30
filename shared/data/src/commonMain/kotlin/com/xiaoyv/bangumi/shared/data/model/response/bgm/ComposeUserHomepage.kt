package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeUserHomepage(
    @SerialName("left") val left: SerializeList<String> = persistentListOf(),
    @SerialName("right") val right: SerializeList<String> = persistentListOf(),
) {
    companion object {
        val Empty = ComposeUserHomepage()
    }
}
