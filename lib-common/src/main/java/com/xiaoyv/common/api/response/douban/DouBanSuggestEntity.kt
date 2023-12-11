package com.xiaoyv.common.api.response.douban

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [DouBanSuggestEntity]
 *
 * @author why
 * @since 12/11/23
 */
@Keep
@Parcelize
data class DouBanSuggestEntity(
    @SerializedName("banned")
    var banned: String? = null,
    @SerializedName("cards")
    var cards: List<Card?>? = null,
    @SerializedName("is_suicide")
    var isSuicide: Boolean = false,
    @SerializedName("words")
    var words: List<String?>? = null
) : Parcelable {

    @Keep
    @Parcelize
    data class Card(
        @SerializedName("layout")
        var layout: String? = null,
        @SerializedName("target")
        var target: Target? = null,
        @SerializedName("target_id")
        var targetId: String? = null,
        @SerializedName("target_type")
        var targetType: String? = null,
        @SerializedName("type_name")
        var typeName: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class Target(
        @SerializedName("abstract")
        var `abstract`: String? = null,
        @SerializedName("card_subtitle")
        var cardSubtitle: String? = null,
        @SerializedName("controversy_reason")
        var controversyReason: String? = null,
        @SerializedName("cover_url")
        var coverUrl: String? = null,
        @SerializedName("has_linewatch")
        var hasLinewatch: Boolean = false,
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("null_rating_reason")
        var nullRatingReason: String? = null,
        @SerializedName("rating")
        var rating: Rating? = null,
        @SerializedName("title")
        var title: String? = null,
        @SerializedName("uri")
        var uri: String? = null,
        @SerializedName("year")
        var year: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class Rating(
        @SerializedName("count")
        var count: Int = 0,
        @SerializedName("max")
        var max: Int = 0,
        @SerializedName("star_count")
        var starCount: Double = 0.0,
        @SerializedName("value")
        var value: Double = 0.0
    ) : Parcelable
}