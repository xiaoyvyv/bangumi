package com.xiaoyv.common.api.request

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [SubjectCollectParam]
 *
 * @author why
 * @since 1/27/24
 */
@Keep
@Parcelize
data class SubjectCollectParam(
    @SerializedName("comment")
    var comment: String? = null,
    @SerializedName("ep_status")
    var epStatus: Int? = null,
    @SerializedName("private")
    var `private`: Boolean = false,
    @SerializedName("rate")
    var rate: Int? = null,
    @SerializedName("tags")
    var tags: List<String>? = null,
    @SerializedName("type")
    var type: Int? = null,
    @SerializedName("vol_status")
    var volStatus: Int? = null,
) : Parcelable