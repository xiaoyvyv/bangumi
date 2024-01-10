package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [AnimeMalSearchEntity]
 *
 * @author why
 * @since 1/10/24
 */
@Keep
@Parcelize
data class AnimeMalSearchEntity(
    @SerializedName("categories")
    var categories: List<Category>? = null,
) : Parcelable {

    @Keep
    @Parcelize
    data class Category(
        @SerializedName("items")
        var items: List<Item>? = null,
        @SerializedName("type")
        var type: String? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class Item(
        @SerializedName("es_score")
        var esScore: Double = 0.0,
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image_url")
        var imageUrl: String? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("payload")
        var payload: Payload? = null,
        @SerializedName("thumbnail_url")
        var thumbnailUrl: String? = null,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("url")
        var url: String? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class Payload(
        @SerializedName("aired")
        var aired: String? = null,
        @SerializedName("media_type")
        var mediaType: String? = null,
        @SerializedName("score")
        var score: String? = null,
        @SerializedName("start_year")
        var startYear: Int = 0,
        @SerializedName("status")
        var status: String? = null,
    ) : Parcelable
}