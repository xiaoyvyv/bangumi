package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.shared.core.utils.formatHMS
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Class: [ComposeParade]
 *
 * @author why
 * @since 12/26/23
 */
@Immutable
@Serializable
data class ComposeParade(
    @SerialName("city")
    val city: String? = null,
    @SerialName("cn")
    val cn: String? = null,
    @SerialName("color")
    val color: String? = null,
    @SerialName("cover")
    val cover: String? = null,
    @SerialName("geo")
    val geo: List<Double>? = null,
    @SerialName("id")
    val id: String = "",
    @SerialName("imagesLength")
    val imagesLength: Int = 0,
    @SerialName("litePoints")
    val litePoints: List<LitePoint>? = null,
    @SerialName("modified")
    val modified: Long = 0,
    @SerialName("pointsLength")
    val pointsLength: Int = 0,
    @SerialName("title")
    val title: String? = null,
    @SerialName("zoom")
    val zoom: Double = 0.0,
) {
    val isNotEmpty: Boolean
        get() = !litePoints.isNullOrEmpty()

    @Immutable
    @Serializable
    data class LitePoint(
        @SerialName("id")
        val id: String = "",
        @SerialName("cn")
        val cn: String? = null,
        @SerialName("ep")
        val ep: Int = 0,
        @SerialName("geo")
        val geo: List<Double>? = null,
        @SerialName("image")
        val image: String? = null,
        @SerialName("name")
        val name: String? = null,
        @SerialName("s")
        val s: Long = 0,
    ) {
        val displayEp: String
            @Composable
            get() = remember(this) {
                buildString {
                    append("Ep ")
                    append(ep)
                    if (s != 0L) {
                        append("  ")
                        append(s.formatHMS())
                    }
                }
            }
    }

    companion object {
        val Empty = ComposeParade()
    }
}