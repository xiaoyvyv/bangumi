package com.xiaoyv.bangumi.shared.data.model.response.pixiv

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposePixivProfileImageUrls] Pixiv 用户头像链接
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class ComposePixivProfileImageUrls(
    @SerialName("px_16x16") val px16: String = "",
    @SerialName("px_50x50") val px50: String = "",
    @SerialName("px_170x170") val px170: String = "",
) {
    val maxUrl: String get() = px170.ifBlank { px50.ifBlank { px16 } }
}
