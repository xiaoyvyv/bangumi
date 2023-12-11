package com.xiaoyv.common.api.response.douban

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [DouBanSearchEntity]
 *
 * @author why
 * @since 12/11/23
 */
@Keep
@Parcelize
data class DouBanSearchEntity(
    @SerializedName("banned")
    var banned: String? = null,
    @SerializedName("count")
    var count: Int = 0,
    @SerializedName("items")
    var items: List<Item>? = null,
    @SerializedName("start")
    var start: Int = 0,
    @SerializedName("total")
    var total: Int = 0,
    @SerializedName("types")
    var types: List<Type>? = null
) : Parcelable {

    @Keep
    @Parcelize
    data class Item(
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
    data class Type(
        @SerializedName("total")
        var total: String? = null,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("type_name")
        var typeName: String? = null,
        @SerializedName("uuids")
        var uuids: List<String>? = null
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