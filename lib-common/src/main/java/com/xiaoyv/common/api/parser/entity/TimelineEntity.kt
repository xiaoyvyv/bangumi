package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TimelineAdapterType
import com.xiaoyv.common.helper.callback.IdEntity
import com.xiaoyv.common.widget.image.AnimeGridImageView
import kotlinx.parcelize.Parcelize

/**
 * Class: [TimelineEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Keep
@Parcelize
data class TimelineEntity(
    override var id: String = "",
    var deleteId: String = "",
    var userId: String = "",
    var avatar: String = "",
    var name: String = "",
    var title: CharSequence = "",
    var titleId: String = "",
    @BgmPathType
    var titleType: String = BgmPathType.TYPE_UNKNOWN,
    var titleLink: String = "",
    var content: String = "",
    var time: String = "",
    var platform: String = "",
    var adapterType: Int = TimelineAdapterType.TYPE_TEXT,
    var commentUserId: String = "",
    var commentAble: Boolean = false,
    var commentCount: Int = 0,

    var mediaCard: MediaTimeline = MediaTimeline(),
    var gridCard: List<GridTimeline> = emptyList(),
) : Parcelable, IdEntity {

    @Keep
    @Parcelize
    data class GridTimeline(
        override var image: String = "",
        override var id: String = "",
        @BgmPathType
        var pathType: String = BgmPathType.TYPE_UNKNOWN,
    ) : AnimeGridImageView.Image, Parcelable, IdEntity

    @Keep
    @Parcelize
    data class MediaTimeline(
        override var id: String = "",
        var title: String = "",
        var info: String = "",
        var cover: String = "",
        var cardRate: String = "",
        var cardRateTotal: String = "",
        var score: Float = 0f,
    ) : IdEntity, Parcelable
}
