package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.InterestType
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
    var id: String = "",
    var titleCn: String = "",
    var titleNative: String = "",
    var time: String = "",
    var subtype: String = "",
    var cover: String = "",
    var collectState: MediaCollectForm = MediaCollectForm(),
    var infos: List<String> = emptyList(),
    var recommendIndex: List<MediaIndex> = emptyList(),
    var whoSee: List<MediaWhoSee> = emptyList(),
    var countWish: Int = 0,
    var countDoing: Int = 0,
    var countOnHold: Int = 0,
    var countCollect: Int = 0,
    var countDropped: Int = 0,
    var progressList: List<MediaProgress> = emptyList(),
    var subjectSummary: CharSequence = "",
    var tags: List<MediaTag> = emptyList(),
    var characters: List<MediaCharacter> = emptyList(),
    var relativeMedia: List<MediaRelative> = emptyList(),
    var sameLikes: List<MediaRelative> = emptyList(),
    var reviews: List<MediaReviewEntity> = emptyList(),
    var boards: List<MediaBoardEntity> = emptyList(),
    var comments: List<MediaCommentEntity> = emptyList(),
    var rating: MediaRating = MediaRating(),
) : Parcelable {

    @Parcelize
    @Keep
    data class MediaCollectForm(
        var tags: String = "",
        var comment: String = "",
        var referer: String = "subject",
        @InterestType
        var interest: String = InterestType.TYPE_UNKNOWN,
        var update: String = "保存",
        var privacy: Int = 0,
        var myTags: List<String> = emptyList(),
        var normalTags: List<String> = emptyList()
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaRating(
        var globalRating: Float? = null,
        var description: String = "",
        var globalRank: Int = 0,
        var ratingCount: Int = 0,
        var ratingDetail: List<RatingItem> = emptyList(),
        var standardDeviation: Double = 0.0
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
        var label: Int = 1,
        var count: Int = 0,
        var percent: Float = 0f
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaRelative(
        var cover: String = "",
        var id: String = "",
        var titleCn: String = "",
        var titleNative: String = "",
        var type: String = ""
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaCharacter(
        var avatar: String = "",
        var id: String = "",
        var characterName: String = "",
        var characterNameCn: String = "",
        var saveCount: Int = 0,
        var job: String = "",
        var persons: List<MediaCharacterPerson> = emptyList()
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaCharacterPerson(
        var personId: String = "",
        var personName: String = "",
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaTag(
        var tagName: String = "",
        var title: String = "",
        var count: Int = 0,
        var url: String = ""
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaProgress(
        var titleNative: String = "",
        var titleCn: String = "",
        var firstTime: String = "",
        var duration: String = "",
        var id: String = "",
        var no: String = "",
        var notEp: Boolean = false,
        var isRelease: Boolean = false,
        var commentCount: Int = 0
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaIndex(
        var userName: String = "",
        var userId: String = "",
        var userAvatar: String = "",
        var id: String = "",
        var title: String = ""
    ) : Parcelable

    @Parcelize
    @Keep
    data class MediaWhoSee(
        var userName: String = "",
        var userId: String = "",
        var userAvatar: String = "",
        var time: String = "",
        var star: Float = 0f
    ) : Parcelable
}
