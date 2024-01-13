@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [ImageBooruEntity]
 *
 * @author why
 * @since 1/13/24
 */
class ImageBooruEntity : ArrayList<ImageBooruEntityItem>()

@Keep
@Parcelize
data class ImageBooruEntityItem(
    @SerializedName("approver_id")
    var approverId: Int = 0,
    @SerializedName("bit_flags")
    var bitFlags: Int = 0,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("down_score")
    var downScore: Int = 0,
    @SerializedName("fav_count")
    var favCount: Int = 0,
    @SerializedName("file_ext")
    var fileExt: String? = null,
    @SerializedName("file_size")
    var fileSize: Long = 0,
    @SerializedName("file_url")
    var fileUrl: String? = null,
    @SerializedName("has_active_children")
    var hasActiveChildren: Boolean = false,
    @SerializedName("has_children")
    var hasChildren: Boolean = false,
    @SerializedName("has_large")
    var hasLarge: Boolean = false,
    @SerializedName("has_visible_children")
    var hasVisibleChildren: Boolean = false,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image_height")
    var imageHeight: Int = 0,
    @SerializedName("image_width")
    var imageWidth: Int = 0,
    @SerializedName("is_banned")
    var isBanned: Boolean = false,
    @SerializedName("is_deleted")
    var isDeleted: Boolean = false,
    @SerializedName("is_flagged")
    var isFlagged: Boolean = false,
    @SerializedName("is_pending")
    var isPending: Boolean = false,
    @SerializedName("large_file_url")
    var largeFileUrl: String? = null,
    @SerializedName("last_comment_bumped_at")
    var lastCommentBumpedAt: String? = null,
    @SerializedName("last_commented_at")
    var lastCommentedAt: String? = null,
    @SerializedName("last_noted_at")
    var lastNotedAt: String? = null,
    @SerializedName("md5")
    var md5: String? = null,
    @SerializedName("media_asset")
    var mediaAsset: MediaAsset? = null,
    @SerializedName("parent_id")
    var parentId: Int = 0,
    @SerializedName("pixiv_id")
    var pixivId: Int = 0,
    @SerializedName("preview_file_url")
    var previewFileUrl: String? = null,
    @SerializedName("rating")
    var rating: String? = null,
    @SerializedName("score")
    var score: Int = 0,
    @SerializedName("source")
    var source: String? = null,
    @SerializedName("tag_count")
    var tagCount: Int = 0,
    @SerializedName("tag_count_artist")
    var tagCountArtist: Int = 0,
    @SerializedName("tag_count_character")
    var tagCountCharacter: Int = 0,
    @SerializedName("tag_count_copyright")
    var tagCountCopyright: Int = 0,
    @SerializedName("tag_count_general")
    var tagCountGeneral: Int = 0,
    @SerializedName("tag_count_meta")
    var tagCountMeta: Int = 0,
    @SerializedName("tag_string")
    var tagString: String? = null,
    @SerializedName("tag_string_artist")
    var tagStringArtist: String? = null,
    @SerializedName("tag_string_character")
    var tagStringCharacter: String? = null,
    @SerializedName("tag_string_copyright")
    var tagStringCopyright: String? = null,
    @SerializedName("tag_string_general")
    var tagStringGeneral: String? = null,
    @SerializedName("tag_string_meta")
    var tagStringMeta: String? = null,
    @SerializedName("up_score")
    var upScore: Int = 0,
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("uploader_id")
    var uploaderId: Int = 0,
) : Parcelable {

    @Keep
    @Parcelize
    data class MediaAsset(
        @SerializedName("created_at")
        var createdAt: String? = null,
        @SerializedName("duration")
        var duration: Double? = null,
        @SerializedName("file_ext")
        var fileExt: String? = null,
        @SerializedName("file_key")
        var fileKey: String? = null,
        @SerializedName("file_size")
        var fileSize: Int = 0,
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image_height")
        var imageHeight: Int = 0,
        @SerializedName("image_width")
        var imageWidth: Int = 0,
        @SerializedName("is_public")
        var isPublic: Boolean = false,
        @SerializedName("md5")
        var md5: String? = null,
        @SerializedName("pixel_hash")
        var pixelHash: String? = null,
        @SerializedName("status")
        var status: String? = null,
        @SerializedName("updated_at")
        var updatedAt: String? = null,
        @SerializedName("variants")
        var variants: List<Variant?>? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class Variant(
        @SerializedName("file_ext")
        var fileExt: String? = null,
        @SerializedName("height")
        var height: Int = 0,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("url")
        var url: String? = null,
        @SerializedName("width")
        var width: Int = 0,
    ) : Parcelable
}