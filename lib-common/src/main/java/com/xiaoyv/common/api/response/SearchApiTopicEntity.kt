package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [SearchApiTopicEntity]
 *
 * @author why
 * @since 1/16/24
 */
@Keep
@Parcelize
data class SearchApiTopicEntity(
    @SerializedName("bgmGrp")
    var bgmGrp: String? = null,
    @SerializedName("bgmGrpAvatar")
    var bgmGrpAvatar: String? = null,
    @SerializedName("bgmGrpName")
    var bgmGrpName: String? = null,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("summary")
    var summary: String? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("timeDate")
    var timeDate: Long = 0,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("uid")
    var uid: String? = null,
    @SerializedName("userAvatar")
    var userAvatar: String? = null,
    @SerializedName("userName")
    var userName: String? = null,
) : Parcelable