package com.xiaoyv.common.api.response
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


/**
 * Class: [UploadResultEntity]
 *
 * @author why
 * @since 12/2/23
 */
@Keep
@Parcelize
data class UploadResultEntity(
    @SerializedName("filename")
    var filename: String? = null,
    @SerializedName("photo_id")
    var photoId: Int = 0,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("thumb_url")
    var thumbUrl: String? = null
) : Parcelable