package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [MediaJsonEntity]
 *
 * @author why
 * @since 11/29/23
 */
@Keep
@Parcelize
data class MediaJsonEntity(
    @SerializedName("collection")
    var collection: Collection? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("eps")
    var eps: Int = 0,
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("images")
    var images: Images? = null,
    @SerializedName("infobox")
    var infobox: List<Infobox>? = null,
    @SerializedName("locked")
    var locked: Boolean = false,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("name_cn")
    var nameCn: String? = null,
    @SerializedName("nsfw")
    var nsfw: Boolean = false,
    @SerializedName("platform")
    var platform: String? = null,
    @SerializedName("rating")
    var rating: Rating? = null,
    @SerializedName("summary")
    var summary: String? = null,
    @SerializedName("tags")
    var tags: List<Tag>? = null,
    @SerializedName("total_episodes")
    var totalEpisodes: Int = 0,
    @SerializedName("type")
    var type: Int = 0,
    @SerializedName("volumes")
    var volumes: Int = 0
) : Parcelable {

    @Keep
    @Parcelize
    data class Collection(
        @SerializedName("collect")
        var collect: Int = 0,
        @SerializedName("doing")
        var doing: Int = 0,
        @SerializedName("dropped")
        var dropped: Int = 0,
        @SerializedName("on_hold")
        var onHold: Int = 0,
        @SerializedName("wish")
        var wish: Int = 0
    ) : Parcelable

    @Keep
    @Parcelize
    data class Images(
        @SerializedName("common")
        var common: String? = null,
        @SerializedName("grid")
        var grid: String? = null,
        @SerializedName("large")
        var large: String? = null,
        @SerializedName("medium")
        var medium: String? = null,
        @SerializedName("small")
        var small: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class Infobox(
        @SerializedName("key")
        var key: String? = null,
        @SerializedName("value")
        var value: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class Rating(
        @SerializedName("count")
        var count: Map<String, Int>? = null,
        @SerializedName("rank")
        var rank: Int = 0,
        @SerializedName("score")
        var score: Double = 0.0,
        @SerializedName("total")
        var total: Int = 0
    ) : Parcelable

    @Keep
    @Parcelize
    data class Tag(
        @SerializedName("count")
        var count: Int = 0,
        @SerializedName("name")
        var name: String? = null
    ) : Parcelable
}