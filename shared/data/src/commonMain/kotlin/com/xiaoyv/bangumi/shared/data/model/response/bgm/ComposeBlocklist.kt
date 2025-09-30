package com.xiaoyv.bangumi.shared.data.model.response.bgm

import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComposeBlocklist(
    @SerialName("blocklist") val blocklist: SerializeList<Long> = persistentListOf(),
)
