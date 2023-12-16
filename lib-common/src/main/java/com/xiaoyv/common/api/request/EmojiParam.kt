package com.xiaoyv.common.api.request

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class EmojiParam(
    @SerializedName("enable") var enable: Boolean = false,
    @SerializedName("likeType") var likeType: String = "",
    @SerializedName("likeMainId") var likeMainId: String = "",
    @SerializedName("likeCommentId") var likeCommentId: String = "",
) : Parcelable