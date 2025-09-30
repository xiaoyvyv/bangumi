package com.xiaoyv.bangumi.shared.data.model.response.mikan

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposeMikanGroup]
 *
 * @author why
 * @since 2025/1/23
 */
@Immutable
@Serializable
data class ComposeMikanGroup(
    @SerialName("id") val id: String? = null,
    @SerialName("mikanId") val mikanId: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("time") val time: String? = null,
    @SerialName("poster") val poster: String? = null,
) {
    /**
     * RSS
     */
    val rssUrl: String
        get() = "https://mikanime.tv/RSS/Bangumi?bangumiId=$mikanId&subgroupid=$id"
}