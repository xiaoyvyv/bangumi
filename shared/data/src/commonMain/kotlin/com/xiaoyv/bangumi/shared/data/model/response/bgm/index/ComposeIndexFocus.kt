package com.xiaoyv.bangumi.shared.data.model.response.bgm.index

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeIndexFocus(
    val id: Long = 0,
    val title: String = "",
    val author: String = "",
    val images: SerializeList<ComposeImages> = persistentListOf(),
)
