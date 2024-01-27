package com.xiaoyv.common.api.response.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ApiSubjectEntity(
    @SerializedName("collection_total")
    var collectionTotal: Int = 0,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("eps")
    var eps: Int = 0,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("images")
    var images: Images? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("name_cn")
    var nameCn: String? = null,
    @SerializedName("rank")
    var rank: Int = 0,
    @SerializedName("score")
    var score: Double = 0.0,
    @SerializedName("short_summary")
    var shortSummary: String? = null,
    @SerializedName("tags")
    var tags: List<Tag?>? = null,
    @SerializedName("type")
    var type: Int = 0,
    @SerializedName("volumes")
    var volumes: Int = 0,
) : Parcelable {

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
        var small: String? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class Tag(
        @SerializedName("count")
        var count: Int = 0,
        @SerializedName("name")
        var name: String? = null,
    ) : Parcelable
}