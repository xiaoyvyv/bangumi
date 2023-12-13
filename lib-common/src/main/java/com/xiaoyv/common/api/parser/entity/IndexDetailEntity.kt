package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [IndexDetailEntity]
 *
 * @author why
 * @since 11/30/23
 */
@Keep
@Parcelize
data class IndexDetailEntity(
    @SerializedName("id") var id: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("time") var time: String = "",
    @SerializedName("userAvatar") var userAvatar: String = "",
    @SerializedName("userName") var userName: String = "",
    @SerializedName("userId") var userId: String = "",
    @SerializedName("content") var content: String = "",
    @SerializedName("mediaCount") var mediaCount: Int = 0,
    @SerializedName("collectCount") var collectCount: Int = 0,
    @SerializedName("isCollected") var isCollected: Boolean = false,
    @SerializedName("comments") var comments: List<CommentTreeEntity> = emptyList(),
    @SerializedName("deleteForms") var deleteForms: Map<String, String> = emptyMap(),
) : Parcelable
