package com.xiaoyv.common.api.response.base

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Keep
@Parcelize
data class BasePage<T>(
    @SerializedName("current")
    var current: Int = 0,
    @SerializedName("optimizeCountSql")
    var optimizeCountSql: Boolean = false,
    @SerializedName("optimizeJoinOfCountSql")
    var optimizeJoinOfCountSql: Boolean = false,
    @SerializedName("pages")
    var pages: Int = 0,
    @SerializedName("records")
    var records: List<@RawValue T>? = null,
    @SerializedName("searchCount")
    var searchCount: Boolean = false,
    @SerializedName("size")
    var size: Int = 0,
    @SerializedName("total")
    var total: Int = 0,
) : Parcelable