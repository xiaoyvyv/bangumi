package com.xiaoyv.common.api.response.douban

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize


/**
 * Class: [DouBanPhotoEntity]
 *
 * @author why
 * @since 12/11/23
 */
@Keep
@Parcelize
data class DouBanPhotoEntity(
    @SerializedName("c")
    var c: Int = 0,
    @SerializedName("count")
    var count: Int = 0,
    @SerializedName("f")
    var f: Int = 0,
    @SerializedName("n")
    var n: Int = 0,
    @SerializedName("o")
    var o: Int = 0,
    @SerializedName("photos")
    var photos: List<Photo>? = null,
    @SerializedName("start")
    var start: Int = 0,
    @SerializedName("total")
    var total: Int = 0,
    @SerializedName("w")
    var w: Int = 0,
) : Parcelable {

    @Keep
    @Parcelize
    data class Photo(
        @SerializedName("id")
        override var id: String = "",
        @SerializedName("author")
        var author: Author? = null,
        @SerializedName("collections_count")
        var collectionsCount: Int = 0,
        @SerializedName("comments_count")
        var commentsCount: Int = 0,
        @SerializedName("create_time")
        var createTime: String? = null,
        @SerializedName("description")
        var description: String? = null,
        @SerializedName("image")
        var image: Image? = null,
        @SerializedName("is_collected")
        var isCollected: Boolean = false,
        @SerializedName("likers_count")
        var likersCount: Int = 0,
        @SerializedName("owner_uri")
        var ownerUri: String? = null,
        @SerializedName("position")
        var position: Int = 0,
        @SerializedName("reaction_type")
        var reactionType: Int = 0,
        @SerializedName("reactions_count")
        var reactionsCount: Int = 0,
        @SerializedName("read_count")
        var readCount: Int = 0,
        @SerializedName("reply_limit")
        var replyLimit: String? = null,
        @SerializedName("reshares_count")
        var resharesCount: Int = 0,
        @SerializedName("sharing_url")
        var sharingUrl: String? = null,
        @SerializedName("status")
        var status: String? = null,
        @SerializedName("subtype")
        var subtype: String? = null,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("uri")
        var uri: String? = null,
        @SerializedName("url")
        var url: String? = null,
    ) : Parcelable, IdEntity

    @Keep
    @Parcelize
    data class Author(
        @SerializedName("avatar")
        var avatar: String? = null,
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("is_club")
        var isClub: Boolean = false,
        @SerializedName("kind")
        var kind: String? = null,
        @SerializedName("loc")
        var loc: Loc? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("reg_time")
        var regTime: String? = null,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("uid")
        var uid: String? = null,
        @SerializedName("uri")
        var uri: String? = null,
        @SerializedName("url")
        var url: String? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class Loc(
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("uid")
        var uid: String? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class Image(
        @SerializedName("is_animated")
        var isAnimated: Boolean = false,
        @SerializedName("large")
        var large: DouBanImageEntity? = null,
        @SerializedName("normal")
        var normal: DouBanImageEntity? = null,
        @SerializedName("primary_color")
        var primaryColor: String? = null,
        @SerializedName("raw")
        var raw: String? = null,
        @SerializedName("small")
        var small: DouBanImageEntity? = null,
    ) : Parcelable
}