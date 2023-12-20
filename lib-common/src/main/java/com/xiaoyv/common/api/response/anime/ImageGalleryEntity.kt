package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize


/**
 * Class: [ImageGalleryEntity]
 *
 * @author why
 * @since 12/20/23
 */
@Keep
@Parcelize
data class ImageGalleryEntity(
    @SerializedName("max_pages")
    var maxPages: Int = 0,
    @SerializedName("page_number")
    var pageNumber: Int = 0,
    @SerializedName("posts")
    var posts: List<Post>? = null,
    @SerializedName("posts_count")
    var postsCount: Int = 0,
    @SerializedName("posts_per_page")
    var postsPerPage: Int = 0,
    @SerializedName("response_posts_count")
    var responsePostsCount: Int = 0,
) : Parcelable {

    @Keep
    @Parcelize
    data class Post(
        @SerializedName("id")
        override var id: String = "",
        @SerializedName("artefacts_degree")
        var artefactsDegree: Double = 0.0,
        @SerializedName("color")
        var color: List<Int>? = null,
        @SerializedName("datetime")
        var datetime: String? = null,
        @SerializedName("download_count")
        var downloadCount: Int = 0,
        @SerializedName("erotics")
        var erotics: Int = 0,
        @SerializedName("ext")
        var ext: String? = null,
        @SerializedName("have_alpha")
        var haveAlpha: Boolean = false,
        @SerializedName("height")
        var height: Int = 0,
        @SerializedName("md5")
        var md5: String? = null,
        @SerializedName("md5_pixels")
        var md5Pixels: String? = null,
        @SerializedName("pubtime")
        var pubtime: String? = null,
        @SerializedName("score")
        var score: Int = 0,
        @SerializedName("score_number")
        var scoreNumber: Int = 0,
        @SerializedName("size")
        var size: Long = 0,
        @SerializedName("smooth_degree")
        var smoothDegree: Double = 0.0,
        @SerializedName("spoiler")
        var spoiler: Boolean = false,
        @SerializedName("status")
        var status: Int = 0,
        @SerializedName("status_type")
        var statusType: Int = 0,
        @SerializedName("tags_count")
        var tagsCount: Int = 0,
        @SerializedName("width")
        var width: Int = 0,
    ) : Parcelable, IdEntity {
        private val realExt get() = if (haveAlpha) ".png" else ".jpg"
        private val dir get() = if (md5.orEmpty().length > 3) md5.orEmpty().substring(0, 3) else ""

        val url get() = "https://cpreview.anime-pictures.net/$dir/${md5}_bp$realExt.avif"
        val largeUrl get() = "https://cimages.anime-pictures.net/$dir/$md5$ext"
    }
}