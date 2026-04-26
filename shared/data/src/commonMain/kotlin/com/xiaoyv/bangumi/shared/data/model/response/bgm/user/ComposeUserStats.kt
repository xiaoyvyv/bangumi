package com.xiaoyv.bangumi.shared.data.model.response.bgm.user

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollectionInfo
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeUserStats(
    @SerialName("blog") val blog: Int = 0,
    @SerialName("friend") val friend: Int = 0,
    @SerialName("group") val group: Int = 0,
    @SerialName("index") val index: Index = Index.Empty,
    @SerialName("mono") val mono: Mono = Mono.Empty,
    @SerialName("subject") val subject: Subject = Subject.Empty,
    @SerialName("rating") val rating: Rating = Rating.Empty,
) {
    @Serializable
    data class Index(
        @SerialName("collect") val collect: Int = 0,
        @SerialName("create") val create: Int = 0,
    ) {
        companion object {
            val Empty = Index()
        }
    }

    @Serializable
    data class Mono(
        @SerialName("character") val character: Int = 0,
        @SerialName("person") val person: Int = 0,
    ) {
        companion object {
            val Empty = Mono()
        }
    }

    @Serializable
    data class Subject(
        @SerialName("1") val book: ComposeCollectionInfo = ComposeCollectionInfo.Companion.Empty,
        @SerialName("2") val anime: ComposeCollectionInfo = ComposeCollectionInfo.Companion.Empty,
        @SerialName("3") val music: ComposeCollectionInfo = ComposeCollectionInfo.Companion.Empty,
        @SerialName("4") val game: ComposeCollectionInfo = ComposeCollectionInfo.Companion.Empty,
        @SerialName("6") val real: ComposeCollectionInfo = ComposeCollectionInfo.Companion.Empty,
    ) {
        val all = ComposeCollectionInfo(
            wish = book.wish + anime.wish + music.wish + game.wish + real.wish,
            collect = book.collect + anime.collect + music.collect + game.collect + real.collect,
            doing = book.doing + anime.doing + music.doing + game.doing + real.doing,
            onHold = book.onHold + anime.onHold + music.onHold + game.onHold + real.onHold,
            dropped = book.dropped + anime.dropped + music.dropped + game.dropped + real.dropped
        )

        val done: Int
            get() = book.collect + anime.collect + music.collect + game.collect + real.collect

        companion object {
            val Empty = Subject()
        }
    }

    companion object {
        val Empty = ComposeUserStats()
    }

    @Immutable
    @Serializable
    data class Rating(
        @SerialName("all") val all: RatingInfo = RatingInfo.Empty,
        @SerialName("book") val book: RatingInfo = RatingInfo.Empty,
        @SerialName("anime") val anime: RatingInfo = RatingInfo.Empty,
        @SerialName("game") val game: RatingInfo = RatingInfo.Empty,
        @SerialName("music") val music: RatingInfo = RatingInfo.Empty,
        @SerialName("real") val real: RatingInfo = RatingInfo.Empty,
    ) {
        companion object {
            val Empty = Rating()
        }
    }

    @Immutable
    @Serializable
    data class RatingInfo(
        @SerialName("averageScore") val averageScore: Float = 0f,
        @SerialName("standardDeviation") val standardDeviation: Float = 0f,
        @SerialName("ratingCount") val ratingCount: Int = 0,
        @SerialName("infos") val infos: SerializeList<RatingItem> = persistentListOf(),
    ) {
        companion object {
            val Empty = RatingInfo()
        }
    }

    @Immutable
    @Serializable
    data class RatingItem(
        @SerialName("label") val label: Int = 1,
        @SerialName("count") val count: Int = 0,
        @SerialName("percent") val percent: Float = 0f,
    )
}