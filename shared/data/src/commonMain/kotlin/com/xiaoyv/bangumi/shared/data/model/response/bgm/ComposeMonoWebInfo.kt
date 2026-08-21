package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposeMonoWebInfo]
 *
 * @since 2025/5/18
 */
@Immutable
@Serializable
data class ComposeMonoWebInfo(
    @SerialName("info") val info: String = "",
    @SerialName("shortInfo") val shortInfo: String = "",
    @SerialName("indexList") val indexList: SerializeList<ComposeIndex> = persistentListOf(),
    @SerialName("collabs") val collabs: SerializeList<ComposeMonoCollab> = persistentListOf(),
) {
    companion object Companion {
        val Empty: ComposeMonoWebInfo = ComposeMonoWebInfo()
    }
}
