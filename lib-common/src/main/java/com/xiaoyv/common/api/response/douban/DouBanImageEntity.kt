package com.xiaoyv.common.api.response.douban

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class DouBanImageEntity(
    @SerializedName("height")
    var height: Int = 0,
    @SerializedName("size")
    var size: Long = 0,
    @SerializedName("url")
    var url: String? = null,
    @SerializedName("width")
    var width: Int = 0,
) : Parcelable