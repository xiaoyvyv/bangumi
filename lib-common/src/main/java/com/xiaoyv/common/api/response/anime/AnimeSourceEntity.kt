package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * AnimeSearchImageEntity
 *
 * @author why
 * @since 11/18/23
 */
@Keep
@Parcelize
data class AnimeSourceEntity(
    @SerializedName("error")
    var error: String? = null,
    @SerializedName("frameCount")
    var frameCount: Int = 0,
    @SerializedName("result")
    var result: List<SourceResult>? = null
) : Parcelable {

    @Keep
    @Parcelize
    data class SourceResult(
        @SerializedName("anilist")
        var anilist: Long = 0,
        @SerializedName("episode")
        var episode: String? = null,
        @SerializedName("filename")
        var filename: String? = null,
        @SerializedName("from")
        var from: Double = 0.0,
        @SerializedName("image")
        var image: String? = null,
        @SerializedName("similarity")
        var similarity: Double = 0.0,
        @SerializedName("to")
        var to: Double = 0.0,
        @SerializedName("video")
        var video: String? = null
    ) : Parcelable
}