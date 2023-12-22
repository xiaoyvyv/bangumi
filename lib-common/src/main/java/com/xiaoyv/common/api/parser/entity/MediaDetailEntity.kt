package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.callback.IdEntity
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
    @SerializedName("infos") var infoShort: List<CharSequence> = emptyList(),
    @SerializedName("infoHtml") var infoHtml: List<String> = emptyList(),
    @SerializedName("recommendIndex") var recommendIndex: List<MediaIndex> = emptyList(),
    @SerializedName("whoSee") var whoSee: List<MediaWho> = emptyList(),
    @SerializedName("countWish") var countWish: Int = 0,
    @SerializedName("countDoing") var countDoing: Int = 0,
    @SerializedName("countOnHold") var countOnHold: Int = 0,
    @SerializedName("countCollect") var countCollect: Int = 0,
    @SerializedName("countDropped") var countDropped: Int = 0,
    @SerializedName("myProgress") var myProgress: Int = 0,
    @SerializedName("totalProgress") var totalProgress: Int = 0,
    @SerializedName("epList") var epList: List<MediaChapterEntity> = emptyList(),
    @SerializedName("subjectSummary") var subjectSummary: String = "",
    @SerializedName("tags") var tags: List<MediaTag> = emptyList(),
    @SerializedName("characters") var characters: List<MediaCharacter> = emptyList(),
    @SerializedName("relativeMedia") var relativeMedia: List<MediaRelative> = emptyList(),
    @SerializedName("sameLikes") var sameLikes: List<MediaRelative> = emptyList(),
    @SerializedName("reviews") var reviews: List<MediaReviewEntity> = emptyList(),
    @SerializedName("boards") var boards: List<MediaBoardEntity> = emptyList(),
    @SerializedName("comments") var comments: List<MediaCommentEntity> = emptyList(),
    @SerializedName("rating") var rating: MediaRating = MediaRating(),
    @SerializedName("photos") var photos: List<DouBanPhotoEntity.Photo> = emptyList(),
) : Parcelable {


    @Parcelize
    @Keep
    data class MediaRating(
        @SerializedName("globalRating") var globalRating: Float? = null,
        @SerializedName("description") var description: String = "",
        @SerializedName("globalRank") var globalRank: Int = 0,
        @SerializedName("ratingCount") var ratingCount: Int = 0,
        @SerializedName("ratingDetail") var ratingDetail: List<RatingItem> = emptyList(),
        @SerializedName("standardDeviation") var standardDeviation: Double = 0.0,
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
        @SerializedName("percent") var percent: Float = 0f,
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaRelative(
        @SerializedName("cover") var cover: String = "",
        @SerializedName("id") var id: String = "",
        @SerializedName("titleCn") var titleCn: String = "",
        @SerializedName("titleNative") var titleNative: String = "",
        @SerializedName("type") @MediaType var type: String = MediaType.TYPE_UNKNOWN,
        @SerializedName("collectType") @InterestType var collectType: String = InterestType.TYPE_UNKNOWN,
        @SerializedName("characterJobs") var characterJobs: List<String> = emptyList(),
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaCharacter(
        @SerializedName("id") override var id: String = "",
        @SerializedName("avatar") var avatar: String = "",
        @SerializedName("characterName") var characterName: String = "",
        @SerializedName("characterNameCn") var characterNameCn: String = "",
        @SerializedName("saveCount") var saveCount: Int = 0,
        @SerializedName("jobs") var jobs: List<String> = emptyList(),
        @SerializedName("persons") var persons: List<MediaCharacterPerson> = emptyList(),
        @SerializedName("personJob") var personJob: String = "",
    ) : Parcelable, IdEntity

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
        @SerializedName("mediaType") var mediaType: String = "",
        @SerializedName("url") var url: String = "",
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaProgress(
        @SerializedName("titleNative") var titleNative: String = "",
        @SerializedName("titleCn") var titleCn: String = "",
        @SerializedName("firstTime") var firstTime: String = "",
        @SerializedName("duration") var duration: String = "",
        @SerializedName("id") override var id: String = "",
        @SerializedName("number") var number: String = "",
        @SerializedName("isNotEp") var isNotEp: Boolean = false,
        @SerializedName("isAiring") var isAiring: Boolean = false,
        @SerializedName("isAired") var isAired: Boolean = false,
        @SerializedName("collectType") var collectType: String = InterestType.TYPE_UNKNOWN,
        @SerializedName("commentCount") var commentCount: Int = 0,
    ) : Parcelable, IdEntity

    @Parcelize
    @Keep
    data class MediaIndex(
        @SerializedName("userName") var userName: String = "",
        @SerializedName("userId") var userId: String = "",
        @SerializedName("userAvatar") var userAvatar: String = "",
        @SerializedName("id") var id: String = "",
        @SerializedName("title") var title: String = "",
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaWho(
        @SerializedName("id") override var id: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("avatar") var avatar: String = "",
        @SerializedName("time") var time: String = "",
        @SerializedName("star") var star: Float = 0f,
    ) : Parcelable, IdEntity
}

