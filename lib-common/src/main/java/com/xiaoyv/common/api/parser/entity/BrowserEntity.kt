package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [BrowserEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Keep
@Parcelize
data class BrowserEntity(
    var items: List<Item> = emptyList(),
    var tags: List<MediaDetailEntity.MediaTag> = emptyList(),
) : Parcelable {

    @Keep
    @Parcelize
    data class Item(
        override var id: String = "",
        var title: String = "",
        var subtitle: String = "",
        var isCollection: Boolean = false,
        var ratingCount: String = "",
        var ratingScore: String = "",
        var rating: Float = 0f,
        var infoTip: InfoTip = InfoTip(),
        var rank: String = "",
        var coverImage: String = "",
        @MediaType
        var mediaType: String = MediaType.TYPE_ANIME,
        var mediaTypeName: String = "",

        var collectTime: String = "",
        var collectComment: String = "",
        var collectTags: String = "",
    ) : Parcelable, IdEntity

    @Keep
    @Parcelize
    data class InfoTip(
        var yearMonth: String = "",
        var monthDay: String = "",
        var eps: String = "",
        var info: String = "",
    ) : Parcelable {
        override fun toString(): String {
            return info
        }
    }
}