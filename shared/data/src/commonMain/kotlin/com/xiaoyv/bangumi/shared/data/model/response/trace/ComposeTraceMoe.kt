@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.model.response.trace

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * [ComposeTraceMoe]
 *
 * @since 2025/5/15
 */
@Immutable
@Serializable
data class ComposeTraceMoe(
    @SerialName("error")
    val error: String = "",
    @SerialName("frameCount")
    val frameCount: Int = 0,
    @SerialName("result")
    val result: SerializeList<Result> = persistentListOf(),
) {
    @Immutable
    @Serializable
    data class Result(
        @SerialName("anilist")
        val anilist: AnilistInfo = AnilistInfo(),
        @SerialName("episode")
        val episode: String = "",
        @SerialName("filename")
        val filename: String = "",
        @SerialName("from")
        val from: Double = 0.0,
        @SerialName("image")
        val image: String = "",
        @SerialName("similarity")
        val similarity: Double = 0.0,
        @SerialName("to")
        val to: Double = 0.0,
        @SerialName("video")
        val video: String = "",
    )

    @Immutable
    @Serializable
    data class AnilistInfo(
        @SerialName("id")
        val id: Long = 0,
        @SerialName("idMal")
        val idMal: Int = 0,
        @SerialName("isAdult")
        val isAdult: Boolean = false,
        @SerialName("synonyms")
        val synonyms: SerializeList<String> = persistentListOf(),
        @SerialName("title")
        val title: AnilistTitle = AnilistTitle(),
    )

    @Immutable
    @Serializable
    data class AnilistTitle(
        @SerialName("english")
        val english: String = "",
        @SerialName("native")
        val native: String = "",
        @SerialName("romaji")
        val romaji: String = "",
        @SerialName("chinese")
        val chinese: String = "",
    ) {
        val displayName get() = chinese.ifBlank { native }.ifBlank { english }.ifBlank { romaji }
    }

    companion object {
        val Empty = ComposeTraceMoe()
    }
}
