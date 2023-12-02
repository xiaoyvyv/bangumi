package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [MediaBoardEntity]
 *
 * @author why
 * @since 11/29/23
 */
@Parcelize
@Keep
data class MediaBoardEntity(
    @SerializedName("id") var id: String = "",
    @SerializedName("content") var content: String = "",
    @SerializedName("userName") var userName: String = "",
    @SerializedName("userId") var userId: String = "",
    @SerializedName("time") var time: String = "",
    @SerializedName("replies") var replies: String = ""
) : Parcelable
