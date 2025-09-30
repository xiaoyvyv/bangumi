package com.xiaoyv.bangumi.shared.data.model.response.db

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Class: [ComposeDoubanPhoto]
 *
 * @author why
 * @since 12/11/23
 */
@Immutable
@Serializable
data class ComposeDoubanPhoto(
    @SerialName("count") val count: Int = 0,
    @SerialName("photos") val photos: SerializeList<Photo> = persistentListOf(),
    @SerialName("start") val start: Int = 0,
    @SerialName("total") val total: Int = 0,
    /**
     * Local Fields
     */
    @SerialName("doubanMediaId") val doubanMediaId: String = "",
    @SerialName("doubanMediaType") val doubanMediaType: String = "",
) {
    val isNotEmpty get() = !photos.isEmpty()

    @Immutable
    @Serializable
    data class Photo(@SerialName("image") val image: Image = Image()) {
        val displayNormalImage: ImageInfo
            get() = image.normalImage ?: image.largeImage ?: image.small ?: EmptyImageInfo

        val displayLargeImage: ImageInfo
            get() = image.largeImage ?: image.normalImage ?: image.small ?: EmptyImageInfo
    }

    @Immutable
    @Serializable
    data class Image(
        @SerialName("large") val large: ImageInfo? = null,
        @SerialName("normal") val normal: ImageInfo? = null,
        @SerialName("small") val small: ImageInfo? = null,
        @SerialName("is_animated") val isAnimated: Boolean = false,
        @SerialName("primary_color") val primaryColor: String? = null,
        @SerialName("raw") val raw: String? = null,
    ) {
        val largeImage: ImageInfo?
            get() = large ?: normal ?: small
        val normalImage: ImageInfo?
            get() = normal ?: large ?: small
    }

    @Immutable
    @Serializable
    data class ImageInfo(
        @SerialName("height") var height: Int = 0,
        @SerialName("size") var size: Long = 0,
        @SerialName("url") var url: String? = null,
        @SerialName("width") var width: Int = 0,
    ) {
        val displayAspect: Float
            get() = if (height > 0 && width > 0) width / height.toFloat() else 16 / 9f
    }

    companion object {
        val Empty: ComposeDoubanPhoto = ComposeDoubanPhoto()
        val EmptyImageInfo: ImageInfo = ImageInfo()
    }
}