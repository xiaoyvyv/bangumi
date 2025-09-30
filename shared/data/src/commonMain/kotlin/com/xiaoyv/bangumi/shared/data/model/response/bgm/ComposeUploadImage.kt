package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Class: [ComposeUploadImage]
 *
 * @author why
 * @since 12/2/23
 */
@Immutable
@Serializable
data class ComposeUploadImage(
    @SerialName("filename") var filename: String = "",
    @SerialName("photo_id") var photoId: Long = 0,
    @SerialName("status") var status: String = "",
    @SerialName("thumb_url") var thumbUrl: String = "",
)