package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.MediaType
import kotlinx.parcelize.Parcelize
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Class: [MediaDetailEntity]
 *
 * @author why
 * @since 11/29/23
 */

@Parcelize
@Keep
data class MediaDetailEntity(
    @SerializedName("id") var id: String = "",
    @SerializedName("mediaType") @MediaType var mediaType: String = MediaType.TYPE_ANIME,
    @SerializedName("titleCn") var titleCn: String = "",
    @SerializedName("titleNative") var titleNative: String = "",
    @SerializedName("time") var time: String = "",
    @SerializedName("subtype") var subtype: String = "",
    @SerializedName("cover") var cover: String = "",
    @SerializedName("collectState") var collectState: MediaCollectForm = MediaCollectForm(),
    @SerializedName("infos") var infos: List<CharSequence> = emptyList(),
    @SerializedName("recommendIndex") var recommendIndex: List<MediaIndex> = emptyList(),
    @SerializedName("whoSee") var whoSee: List<MediaWho> = emptyList(),
    @SerializedName("countWish") var countWish: Int = 0,
    @SerializedName("countDoing") var countDoing: Int = 0,
    @SerializedName("countOnHold") var countOnHold: Int = 0,
    @SerializedName("countCollect") var countCollect: Int = 0,
    @SerializedName("countDropped") var countDropped: Int = 0,
    @SerializedName("progressList") var progressList: List<MediaProgress> = emptyList(),
    @SerializedName("subjectSummary") var subjectSummary: CharSequence = "",
    @SerializedName("tags") var tags: List<MediaTag> = emptyList(),
    @SerializedName("characters") var characters: List<MediaCharacter> = emptyList(),
    @SerializedName("relativeMedia") var relativeMedia: List<MediaRelative> = emptyList(),
    @SerializedName("sameLikes") var sameLikes: List<MediaRelative> = emptyList(),
    @SerializedName("reviews") var reviews: List<MediaReviewEntity> = emptyList(),
    @SerializedName("boards") var boards: List<MediaBoardEntity> = emptyList(),
    @SerializedName("comments") var comments: List<MediaCommentEntity> = emptyList(),
    @SerializedName("rating") var rating: MediaRating = MediaRating(),
) : Parcelable {


    @Parcelize
    @Keep
    data class MediaRating(
        @SerializedName("globalRating") var globalRating: Float? = null,
        @SerializedName("description") var description: String = "",
        @SerializedName("globalRank") var globalRank: Int = 0,
        @SerializedName("ratingCount") var ratingCount: Int = 0,
        @SerializedName("ratingDetail") var ratingDetail: List<RatingItem> = emptyList(),
        @SerializedName("standardDeviation") var standardDeviation: Double = 0.0
    ) : Parcelable {

        fun calculateStandardDeviation(): Double {
            runCatching {
                // 步骤1：计算平均值
                val totalScores = ratingDetail.sumOf { it.label * it.count }
                val totalCount = ratingDetail.sumOf { it.count }
                val mean = totalScores.toDouble() / totalCount

                // 步骤2和3：计算平方差的累加值
                val sumOfSquaredDifferences =
                    ratingDetail.sumOf { (it.label - mean).pow(2).toInt() * it.count }

                // 步骤4：计算标准差
                val variance = sumOfSquaredDifferences.toDouble() / totalCount
                return sqrt(variance)
            }
            return 0.0
        }
    }

    @Parcelize
    @Keep
    data class RatingItem(
        @SerializedName("label") var label: Int = 1,
        @SerializedName("count") var count: Int = 0,
        @SerializedName("percent") var percent: Float = 0f
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaRelative(
        @SerializedName("cover") var cover: String = "",
        @SerializedName("id") var id: String = "",
        @SerializedName("titleCn") var titleCn: String = "",
        @SerializedName("titleNative") var titleNative: String = "",
        @SerializedName("type") var type: String = "",
        @SerializedName("characterJobs") var characterJobs: List<String> = emptyList()
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaCharacter(
        @SerializedName("avatar") var avatar: String = "",
        @SerializedName("id") var id: String = "",
        @SerializedName("characterName") var characterName: String = "",
        @SerializedName("characterNameCn") var characterNameCn: String = "",
        @SerializedName("saveCount") var saveCount: Int = 0,
        @SerializedName("jobs") var jobs: List<String> = emptyList(),
        @SerializedName("personJob") var personJob: String = "",
        @SerializedName("persons") var persons: List<MediaCharacterPerson> = emptyList()
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaCharacterPerson(
        @SerializedName("personId") var personId: String = "",
        @SerializedName("personName") var personName: String = "",
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaTag(
        @SerializedName("tagName") var tagName: String = "",
        @SerializedName("title") var title: String = "",
        @SerializedName("count") var count: Int = 0,
        @SerializedName("url") var url: String = ""
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaProgress(
        @SerializedName("titleNative") var titleNative: String = "",
        @SerializedName("titleCn") var titleCn: String = "",
        @SerializedName("firstTime") var firstTime: String = "",
        @SerializedName("duration") var duration: String = "",
        @SerializedName("id") var id: String = "",
        @SerializedName("no") var no: String = "",
        @SerializedName("notEp") var notEp: Boolean = false,
        @SerializedName("isRelease") var isRelease: Boolean = false,
        @SerializedName("commentCount") var commentCount: Int = 0
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaIndex(
        @SerializedName("userName") var userName: String = "",
        @SerializedName("userId") var userId: String = "",
        @SerializedName("userAvatar") var userAvatar: String = "",
        @SerializedName("id") var id: String = "",
        @SerializedName("title") var title: String = ""
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaWho(
        @SerializedName("userName") var userName: String = "",
        @SerializedName("userId") var userId: String = "",
        @SerializedName("userAvatar") var userAvatar: String = "",
        @SerializedName("time") var time: String = "",
        @SerializedName("star") var star: Float = 0f
    ) : Parcelable
}

