package com.xiaoyv.bangumi.shared.data.model.response.image

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.xiaoyv.bangumi.shared.core.types.list.ListAlbumType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.min

/**
 * [ComposeGallery]
 *
 * @since 2025/5/23
 */
@Immutable
@Serializable
data class ComposeGallery(
    @SerialName("id") val id: String,
    @SerialName("image") val image: String,
    @SerialName("original") val original: String,
    @SerialName("type") val type: Int = ListAlbumType.UNKNOWN,
    @SerialName("color") val color: List<Int> = emptyList(),
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0,
    @SerialName("size") val size: Long = 0,
    @SerialName("count") val count: Int = 0,
    @SerialName("info") val info: String = "",
) {
    val uiColor: Color
        get() = if (color.size < 3) Color.Unspecified else Color(
            red = color[0],
            green = color[1],
            blue = color[2]
        )

    val aspect: Float
        get() = if (min(width, height) > 0) width / height.toFloat() else 1f
}