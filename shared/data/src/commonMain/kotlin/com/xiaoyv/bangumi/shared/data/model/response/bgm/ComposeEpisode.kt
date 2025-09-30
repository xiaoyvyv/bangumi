@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.app_widget_first_play
import com.xiaoyv.bangumi.core_resource.resources.global_unknown
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.EpisodeType
import com.xiaoyv.bangumi.shared.core.utils.formatMills
import com.xiaoyv.bangumi.shared.core.utils.isToday
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.jetbrains.compose.resources.stringResource

/**
 * [ComposeEpisode]
 *
 * @since 2025/5/8
 */
@Immutable
@Serializable
data class ComposeEpisode(
    @SerialName("comment") val commentCount: Int = 0,
    @SerialName("desc") val description: String = "",
    @SerialName("disc") val discNumber: Int = 0,
    @SerialName("duration") val duration: String = "",
    @SerialName("ep") val episodeNumber: Double = 0.0,
    @SerialName("id") val id: Long = 0,
    @SerialName("name") val name: String = "",
    @SerialName("name_cn") @JsonNames("name_cn", "nameCN") val chineseName: String? = null,
    @SerialName("sort") val sortOrder: Double = 0.0,
    @SerialName("subject_id") @JsonNames("subjectID", "subject_id") val subjectId: Long = 0,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("type") @field:EpisodeType val episodeType: Int = 0,
    @SerialName("airdate") val airdate: String = "",
    @SerialName("collection") val collection: EpCollection = EpCollection(),


    /**
     * Local fields
     */
    @SerialName("splitter") val splitter: String? = null,
) {
    val key = id.toString() + splitter.orEmpty()

    val isAired: Boolean
        @Composable
        get() = remember(airdate) { if (airdate.isBlank()) false else System.currentTimeMillis() > airdate.formatMills() }

    val isAiring: Boolean
        @Composable
        get() = remember(airdate) { airdate.formatMills().isToday() }


    @Composable
    fun rememberDisplayTitle(): String {
        return remember(this) {
            buildString {
                append(sortOrder.toTrimString())
                append(". ")
                append(chineseName.orEmpty().ifBlank { name }.ifBlank { "？" })
            }
        }
    }

    @Composable
    fun rememberDisplaySubtitle(): String {
        val tip = stringResource(Res.string.app_widget_first_play)
        val unknown = stringResource(Res.string.global_unknown)
        return remember(this) {
            buildString {
                append(tip)
                append(airdate.ifBlank { unknown })
                if (duration.isNotBlank()) {
                    append("\u3000")
                    append(duration)
                }
            }
        }
    }

    @Immutable
    @Serializable
    data class EpCollection(
        @SerialName("updated_at") @JsonNames("updated_at", "updatedAt") val updatedAt: SerializeDateLong = 0,

        @SerialName("status")
        @field:CollectionEpisodeType
        val status: Int = CollectionEpisodeType.UNKNOWN,
    ) {
        companion object {
            val Empty = EpCollection()
        }
    }

    companion object Companion {
        val Empty = ComposeEpisode()
    }
}


/**
 * 添加类型分隔符号
 */
fun List<ComposeEpisode>.grouped(): SerializeList<ComposeEpisode> {
    val items = arrayListOf<ComposeEpisode>()
    var lastType = EpisodeType.TYPE_MAIN
    for (it in this) {
        if (it.episodeType == lastType || it.episodeType == EpisodeType.TYPE_MAIN) {
            items.add(it)
            continue
        }
        items.add(
            ComposeEpisode(
                episodeType = it.episodeType,
                splitter = EpisodeType.toAbbrType(it.episodeType)
            )
        )
        items.add(it)
        lastType = it.episodeType
    }
    return items.toPersistentList()
}