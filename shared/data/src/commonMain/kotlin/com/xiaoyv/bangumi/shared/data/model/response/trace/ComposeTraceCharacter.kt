package com.xiaoyv.bangumi.shared.data.model.response.trace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * [ComposeTraceCharacter]
 *
 * @since 2025/5/15
 */
@Serializable
data class ComposeTraceCharacter(
    @SerialName("ai")
    val ai: Boolean = false,
    @SerialName("code")
    val code: Int = 0,
    @SerialName("data")
    val `data`: List<Data>? = null,
    @SerialName("trace_id")
    val traceId: String? = null,
) {
    @Serializable
    data class Data(
        @SerialName("box")
        val box: List<Double>? = null,
        @SerialName("box_id")
        val boxId: String? = null,
        @SerialName("character")
        val character: List<Character>? = null,
        @SerialName("not_confident")
        val notConfident: Boolean = false,
    ) {
        @Composable
        fun rememberRect(imageBitmap: ImageBitmap?): Rect {
            return remember(box, imageBitmap) {
                if (box == null || box.size != 4 || imageBitmap == null) Rect.Zero else {
                    val originalWidth = imageBitmap.width
                    val originalHeight = imageBitmap.height
                    val left = (box[0] * originalWidth).toInt()
                    val top = (box[1] * originalHeight).toInt()
                    val right = (box[2] * originalWidth).toInt()
                    val bottom = (box[3] * originalHeight).toInt()
                    Rect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
                }
            }
        }
    }

    @Serializable
    data class Character(
        @SerialName("character")
        val character: String? = null,
        @SerialName("work")
        val work: String? = null,
    )

    companion object {
        val Empty: ComposeTraceCharacter = ComposeTraceCharacter()
    }
}