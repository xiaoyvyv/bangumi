package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [SearchApiIndexEntity]
 *
 * @author why
 * @since 1/16/24
 */
@Keep
@Parcelize
data class SearchApiIndexEntity(
    @SerializedName("ban")
    var ban: Int = 0,
    @SerializedName("collects")
    var collects: Int = 0,
    @SerializedName("comments")
    var comments: Int = 0,
    @SerializedName("createdAt")
    var createdAt: Long = 0,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("nickname")
    var nickname: String? = null,
    @SerializedName("nsfw")
    var nsfw: Int = 0,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("total")
    var total: Int = 0,
    @SerializedName("updatedAt")
    var updatedAt: Long = 0,
    @SerializedName("username")
    var username: String? = null,
) : Parcelable