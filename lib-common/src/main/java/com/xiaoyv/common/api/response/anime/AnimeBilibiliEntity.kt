package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.BilibiliInterestType
import kotlinx.parcelize.Parcelize

/**
 * Class: [AnimeBilibiliEntity]
 *
 * @author why
 * @since 1/23/24
 */
@Keep
@Parcelize
data class AnimeBilibiliEntity(
    @SerializedName("list")
    var list: List<Item>? = listOf(),
    @SerializedName("pn")
    var pn: Int = 0,
    @SerializedName("ps")
    var ps: Int = 0,
    @SerializedName("total")
    var total: Int = 0,
) : Parcelable {

    @Keep
    @Parcelize
    data class Item(
        @SerializedName("badge")
        var badge: String? = "",
        @SerializedName("badge_ep")
        var badgeEp: String? = "",
        @SerializedName("badge_type")
        var badgeType: Int = 0,
        @SerializedName("both_follow")
        var bothFollow: Boolean = false,
        @SerializedName("can_watch")
        var canWatch: Int = 0,
        @SerializedName("cover")
        var cover: String? = "",
        @SerializedName("evaluate")
        var evaluate: String? = "",
        @SerializedName("first_ep")
        var firstEp: Int = 0,
        @SerializedName("follow_status")
        @BilibiliInterestType
        var followStatus: Int = BilibiliInterestType.TYPE_ALL,
        @SerializedName("formal_ep_count")
        var formalEpCount: Int = 0,
        @SerializedName("horizontal_cover_16_10")
        var horizontalCover1610: String? = "",
        @SerializedName("horizontal_cover_16_9")
        var horizontalCover169: String? = "",
        @SerializedName("is_finish")
        var isFinish: Int = 0,
        @SerializedName("is_new")
        var isNew: Int = 0,
        @SerializedName("is_play")
        var isPlay: Int = 0,
        @SerializedName("is_started")
        var isStarted: Int = 0,
        @SerializedName("media_attr")
        var mediaAttr: Int = 0,
        @SerializedName("media_id")
        var mediaId: Long = 0,
        @SerializedName("mode")
        var mode: Int = 0,
        @SerializedName("progress")
        var progress: String? = "",
        @SerializedName("renewal_time")
        var renewalTime: String? = "",
        @SerializedName("season_attr")
        var seasonAttr: Int = 0,
        @SerializedName("season_id")
        var seasonId: Long = 0,
        @SerializedName("season_status")
        var seasonStatus: Int = 0,
        @SerializedName("season_title")
        var seasonTitle: String? = "",
        @SerializedName("season_type")
        var seasonType: Int = 0,
        @SerializedName("season_type_name")
        var seasonTypeName: String? = "",
        @SerializedName("season_version")
        var seasonVersion: String? = "",
        @SerializedName("short_url")
        var shortUrl: String? = "",
        @SerializedName("square_cover")
        var squareCover: String? = "",
        @SerializedName("styles")
        var styles: List<String>? = listOf(),
        @SerializedName("subtitle")
        var subtitle: String? = "",
        @SerializedName("subtitle_14")
        var subtitle14: String? = "",
        @SerializedName("subtitle_25")
        var subtitle25: String? = "",
        @SerializedName("summary")
        var summary: String? = "",
        @SerializedName("title")
        var title: String? = "",
        @SerializedName("total_count")
        var totalCount: Int = 0,
        @SerializedName("url")
        var url: String? = "",
        @SerializedName("viewable_crowd_type")
        var viewableCrowdType: Int = 0,
    ) : Parcelable
}
