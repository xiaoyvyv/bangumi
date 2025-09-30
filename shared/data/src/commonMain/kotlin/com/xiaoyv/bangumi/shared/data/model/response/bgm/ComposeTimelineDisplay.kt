package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.TimelineType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineDisplay(
    @SerialName("batch") val batch: Boolean = false,
    @SerialName("cat") @field:TimelineType val cat: Int = TimelineType.UNKNOWN,
    @SerialName("createdAt") val createdAt: SerializeDateLong = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("memo") val memo: TimelineMemo = TimelineMemo.Empty,
    @SerialName("replies") val replies: Int = 0,
    @SerialName("source") val source: TimelineSource = TimelineSource.Empty,
    @SerialName("type") val type: Int = 0,
    @SerialName("uid") val uid: Long = 0,
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,
) {
    @Immutable
    @Serializable
    data class TimelineMemo(
        @SerialName("blog") val blog: ComposeBlogEntry = ComposeBlogEntry.Empty,
        @SerialName("daily") val daily: TimelineDaily = TimelineDaily.Empty,
        @SerialName("index") val index: ComposeIndex = ComposeIndex.Empty,
        @SerialName("mono") val mono: TimelineMono = TimelineMono.Empty,
        @SerialName("progress") val progress: TimelineProgress = TimelineProgress.Empty,
        @SerialName("status") val status: TimelineStatus = TimelineStatus.Empty,
        @SerialName("subject") val subject: SerializeList<ComposeSubject> = persistentListOf(),
        @SerialName("wiki") val wiki: TimelineWiki = TimelineWiki.Empty,
    ) {
        companion object {
            val Empty = TimelineMemo()
        }
    }

    @Immutable
    @Serializable
    data class TimelineMono(
        @SerialName("characters") val characters: SerializeList<ComposeMono> = persistentListOf(),
        @SerialName("persons") val persons: SerializeList<ComposeMono> = persistentListOf(),
    ) {
        companion object {
            val Empty = TimelineMono()
        }
    }

    @Immutable
    @Serializable
    data class TimelineSource(
        @SerialName("name") val name: String = "",
        @SerialName("url") val url: String = "",
    ) {
        companion object {
            val Empty = TimelineSource()
        }
    }


    @Immutable
    @Serializable
    data class TimelineDaily(
        @SerialName("groups") val groups: SerializeList<ComposeGroup> = persistentListOf(),
        @SerialName("users") val users: SerializeList<ComposeUser> = persistentListOf(),
    ) {
        companion object {
            val Empty = TimelineDaily()
        }
    }

    @Immutable
    @Serializable
    data class TimelineProgress(
        @SerialName("batch") val batch: TimelineBatch = TimelineBatch.Empty,
        @SerialName("single") val single: TimelineSingle = TimelineSingle.Empty,
    ) {
        companion object {
            val Empty = TimelineProgress()
        }
    }

    @Immutable
    @Serializable
    data class TimelineStatus(
        @SerialName("nickname") val nickname: TimelineNickname = TimelineNickname.Empty,
        @SerialName("sign") val sign: String = "",
        @SerialName("tsukkomi") val tsukkomi: String = "",
    ) {
        companion object {
            val Empty = TimelineStatus()
        }
    }


    @Immutable
    @Serializable
    data class TimelineWiki(
        @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    ) {
        companion object {
            val Empty = TimelineWiki()
        }
    }

    @Immutable
    @Serializable
    data class TimelineBatch(
        @SerialName("epsTotal") val epsTotal: String = "",
        @SerialName("epsUpdate") val epsUpdate: Int = 0,
        @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
        @SerialName("volsTotal") val volsTotal: String = "",
        @SerialName("volsUpdate") val volsUpdate: Int = 0,
    ) {
        companion object {
            val Empty = TimelineBatch()
        }
    }

    @Immutable
    @Serializable
    data class TimelineSingle(
        @SerialName("episode") val episode: ComposeEpisodeRelated = ComposeEpisodeRelated.Empty,
        @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    ) {
        companion object {
            val Empty = TimelineSingle()
        }
    }


    @Immutable
    @Serializable
    data class TimelineNickname(
        @SerialName("after") val after: String = "",
        @SerialName("before") val before: String = "",
    ) {
        companion object {
            val Empty = TimelineNickname()
        }
    }
}

