package com.xiaoyv.common.api.response.douban
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


/**
 * Class: [DouBanInterestEntity]
 *
 * @author why
 * @since 1/26/24
 */
@Keep
@Parcelize
data class DouBanInterestEntity(
    @SerializedName("count")
    var count: Int = 0,
    @SerializedName("interests")
    var interests: List<Interest>? = null,
    @SerializedName("start")
    var start: Int = 0,
    @SerializedName("total")
    var total: Int = 0
) : Parcelable{

    @Keep
    @Parcelize
    data class Interest(
        @SerializedName("comment")
        var comment: String? = null,
        @SerializedName("create_time")
        var createTime: String? = null,
        @SerializedName("id")
        var id: Long = 0,
        @SerializedName("is_private")
        var isPrivate: Boolean = false,
        @SerializedName("sharing_text")
        var sharingText: String? = null,
        @SerializedName("sharing_url")
        var sharingUrl: String? = null,
        @SerializedName("status")
        var status: String? = null,
        @SerializedName("subject")
        var subject: Subject? = null,
        @SerializedName("vote_count")
        var voteCount: Int = 0
    ) : Parcelable

    @Keep
    @Parcelize
    data class Subject(
        @SerializedName("album_no_interact")
        var albumNoInteract: Boolean = false,
        @SerializedName("card_subtitle")
        var cardSubtitle: String? = null,
        @SerializedName("color_scheme")
        var colorScheme: ColorScheme? = null,
        @SerializedName("controversy_reason")
        var controversyReason: String? = null,
        @SerializedName("cover_url")
        var coverUrl: String? = null,
        @SerializedName("genres")
        var genres: List<String?>? = null,
        @SerializedName("has_linewatch")
        var hasLinewatch: Boolean = false,
        @SerializedName("honor_infos")
        var honorInfos: List<HonorInfo?>? = null,
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("in_blacklist")
        var inBlacklist: Boolean = false,
        @SerializedName("is_released")
        var isReleased: Boolean = false,
        @SerializedName("is_show")
        var isShow: Boolean = false,
        @SerializedName("null_rating_reason")
        var nullRatingReason: String? = null,
        @SerializedName("pic")
        var pic: Pic? = null,
        @SerializedName("pubdate")
        var pubdate: List<String>? = null,
        @SerializedName("rating")
        var rating: Rating? = null,
        @SerializedName("release_date")
        var releaseDate: String? = null,
        @SerializedName("sharing_url")
        var sharingUrl: String? = null,
        @SerializedName("subtype")
        var subtype: String? = null,
        @SerializedName("title")
        var title: String? = null,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("uri")
        var uri: String? = null,
        @SerializedName("url")
        var url: String? = null,
        @SerializedName("vendor_desc")
        var vendorDesc: String? = null,
        @SerializedName("vendor_icons")
        var vendorIcons: List<String>? = null,
        @SerializedName("year")
        var year: String? = null
    ) : Parcelable


    @Keep
    @Parcelize
    data class ColorScheme(
        @SerializedName("_avg_color")
        var avgColor: List<Double>? = null,
        @SerializedName("_base_color")
        var baseColor: List<Double>? = null,
        @SerializedName("is_dark")
        var isDark: Boolean = false,
        @SerializedName("primary_color_dark")
        var primaryColorDark: String? = null,
        @SerializedName("primary_color_light")
        var primaryColorLight: String? = null,
        @SerializedName("secondary_color")
        var secondaryColor: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class HonorInfo(
        @SerializedName("kind")
        var kind: String? = null,
        @SerializedName("rank")
        var rank: Int = 0,
        @SerializedName("title")
        var title: String? = null,
        @SerializedName("uri")
        var uri: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class Pic(
        @SerializedName("large")
        var large: String? = null,
        @SerializedName("normal")
        var normal: String? = null
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