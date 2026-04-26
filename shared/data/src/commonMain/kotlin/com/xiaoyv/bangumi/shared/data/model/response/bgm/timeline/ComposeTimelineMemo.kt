package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineMemo(
    @SerialName("blog") val blog: ComposeBlogEntry = ComposeBlogEntry.Empty,
    @SerialName("daily") val daily: ComposeTimelineDaily = ComposeTimelineDaily.Empty,
    @SerialName("index") val index: ComposeIndex = ComposeIndex.Empty,
    @SerialName("mono") val mono: ComposeTimelineMono = ComposeTimelineMono.Empty,
    @SerialName("progress") val progress: ComposeTimelineProgress = ComposeTimelineProgress.Empty,
    @SerialName("status") val status: ComposeTimelineStatus = ComposeTimelineStatus.Empty,
    @SerialName("subject") val subject: SerializeList<ComposeTimelineSubject> = persistentListOf(),
    @SerialName("wiki") val wiki: ComposeTimelineWiki = ComposeTimelineWiki.Empty,
) {

    companion object {
        val Empty = ComposeTimelineMemo()
    }
}