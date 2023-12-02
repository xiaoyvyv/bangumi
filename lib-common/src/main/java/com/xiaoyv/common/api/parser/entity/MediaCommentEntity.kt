package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [MediaCommentEntity]
 *
 * @author why
 * @since 11/29/23
 */
@Parcelize
@Keep
data class MediaCommentEntity(
    @SerializedName("id") var id: String = "",
    @SerializedName("avatar") var avatar: String = "",
    @SerializedName("userName") var userName: String = "",
    @SerializedName("userId") var userId: String = "",
    @SerializedName("time") var time: String = "",
    @SerializedName("comment") var comment: String = "",
    @SerializedName("star") var star: Float = 0f
) : Parcelable