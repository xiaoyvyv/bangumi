package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize


/**
 * Class: [AnimeTourEntity]
 *
 * @author why
 * @since 12/26/23
 */
@Keep
@Parcelize
data class AnimeTourEntity(
    @SerializedName("city")
    var city: String? = null,
    @SerializedName("cn")
    var cn: String? = null,
    @SerializedName("color")
    var color: String? = null,
    @SerializedName("cover")
    var cover: String? = null,
    @SerializedName("geo")
    var geo: List<Double>? = null,
    @SerializedName("id")
    var id: String = "",
    @SerializedName("imagesLength")
    var imagesLength: Int = 0,
    @SerializedName("litePoints")
    var litePoints: List<LitePoint>? = null,
    @SerializedName("modified")
    var modified: Long = 0,
    @SerializedName("pointsLength")
    var pointsLength: Int = 0,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("zoom")
    var zoom: Double = 0.0,
) : Parcelable {

    @Keep
    @Parcelize
    data class LitePoint(
        @SerializedName("id")
        override var id: String = "",
        @SerializedName("cn")
        var cn: String? = null,
        @SerializedName("ep")
        var ep: Int = 0,
        @SerializedName("geo")
        var geo: List<Double?>? = null,
        @SerializedName("image")
        var image: String? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("s")
        var s: Long = 0,
    ) : Parcelable,IdEntity
}