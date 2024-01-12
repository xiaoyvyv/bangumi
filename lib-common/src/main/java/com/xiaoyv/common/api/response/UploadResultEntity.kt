package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


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