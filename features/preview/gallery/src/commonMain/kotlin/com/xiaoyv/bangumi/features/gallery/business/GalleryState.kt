package com.xiaoyv.bangumi.features.gallery.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposeGallery
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [GalleryState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class GalleryState(
    @SerialName("id") val id: String = "",
    @SerialName("images") val images: List<ComposeGallery> = emptyList(),
)
