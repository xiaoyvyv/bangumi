package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.InterestType
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class MediaCollectForm(
    @SerializedName("gh") var gh: String = "",
    @SerializedName("mediaId") var mediaId: String = "",
    @SerializedName("titleCn") var titleCn: String = "",
    @SerializedName("titleNative") var titleNative: String = "",
    @SerializedName("tags") var tags: String = "",
    @SerializedName("comment") var comment: String = "",
    @SerializedName("referer") var referer: String = "subject",
    @SerializedName("update") var update: String = "保存",
    @SerializedName("privacy") var privacy: Int = 0,
    @SerializedName("score") var score: Int = 0,
    @SerializedName("myTags") var myTags: List<MediaDetailEntity.MediaTag> = emptyList(),
    @SerializedName("normalTags") var normalTags: List<MediaDetailEntity.MediaTag> = emptyList(),
    /**
     * 我的收藏状态
     */
    @SerializedName("interest") @InterestType var interest: String = InterestType.TYPE_UNKNOWN,

    var showMyTags: Boolean = false,
) : Parcelable