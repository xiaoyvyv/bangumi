package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * {
 *     "small": "https://lain.bgm.tv/r/200/pic/cover/l/c4/ca/1_d2tF2.jpg",
 *     "grid": "https://lain.bgm.tv/r/100/pic/cover/l/c4/ca/1_d2tF2.jpg",
 *     "large": "https://lain.bgm.tv/pic/cover/l/c4/ca/1_d2tF2.jpg",
 *     "medium": "https://lain.bgm.tv/r/800/pic/cover/l/c4/ca/1_d2tF2.jpg",
 *     "common": "https://lain.bgm.tv/r/400/pic/cover/l/c4/ca/1_d2tF2.jpg"
 *   }
 *    {
 *     "small": "https://lain.bgm.tv/r/100/pic/crt/l/7b/3a/1_crt_FEkJM.jpg",
 *     "grid": "https://lain.bgm.tv/pic/crt/g/7b/3a/1_crt_FEkJM.jpg",
 *     "large": "https://lain.bgm.tv/pic/crt/l/7b/3a/1_crt_FEkJM.jpg",
 *     "medium": "https://lain.bgm.tv/r/400/pic/crt/l/7b/3a/1_crt_FEkJM.jpg"
 *   }
 *   {
 *     "small": "https://lain.bgm.tv/r/100/pic/crt/l/a6/e8/1_prsn_k7wpt.jpg?r=1723962294",
 *     "grid": "https://lain.bgm.tv/pic/crt/g/a6/e8/1_prsn_k7wpt.jpg?r=1723962294",
 *     "large": "https://lain.bgm.tv/pic/crt/l/a6/e8/1_prsn_k7wpt.jpg?r=1723962294",
 *     "medium": "https://lain.bgm.tv/r/400/pic/crt/l/a6/e8/1_prsn_k7wpt.jpg?r=1723962294"
 *   }
 *    {
 *     "large": "https://lain.bgm.tv/pic/user/l/000/83/73/837364_zr1p9.jpg?r=1755273891&hd=1",
 *     "medium": "https://lain.bgm.tv/r/200/pic/user/l/000/83/73/837364_zr1p9.jpg?r=1755273891&hd=1",
 *     "small": "https://lain.bgm.tv/r/100/pic/user/l/000/83/73/837364_zr1p9.jpg?r=1755273891&hd=1"
 *   }
 */
@Immutable
@Serializable
data class ComposeImages(
    @SerialName("common") val common: String = "",
    @SerialName("grid") val grid: String = "",
    @SerialName("large") val large: String = "",
    @SerialName("medium") val medium: String = "",
    @SerialName("small") val small: String = "",
) {
    val displayBlurUrl: String
        get() = small.ifBlank { grid }.ifBlank { medium }.ifBlank { common }.ifBlank { large }

    val displayLargeImage: String
        get() = large.ifBlank { common }.ifBlank { small }.ifBlank { grid }

    val displayGridImage: String
        get() = grid.ifBlank { small }.ifBlank { medium }.ifBlank { large }

    val displayMediumImage: String
        get() = medium.ifBlank { large }.ifBlank { grid }.ifBlank { small }

    companion object {
        val Empty = ComposeImages()

        fun fromUrl(url: String): ComposeImages {
            val largeUrl = url.replace("/[gcsml]/".toRegex(), "/l/")
            return ComposeImages(
                large = url,
                common = largeUrl.replace("/l/", "/m/"),
                grid = url.replace("/l/", "/g/"),
                medium = url.replace("/l/", "/m/"),
                small = url.replace("/l/", "/s/"),
            )
        }
    }
}