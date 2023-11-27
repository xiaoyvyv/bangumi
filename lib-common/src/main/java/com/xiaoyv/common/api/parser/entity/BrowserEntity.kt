package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.MediaType
import kotlinx.parcelize.Parcelize

/**
 * Class: [BrowserEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Keep
@Parcelize
data class BrowserEntity(var items: List<Item> = emptyList()) : Parcelable {

    @Keep
    @Parcelize
    data class Item(
        var title: String = "",
        var subtitle: String = "",
        var isCollection: Boolean = false,
        var ratingCount: String = "",
        var ratingScore: String = "",
        var rating: String = "",
        var infoTip: InfoTip = InfoTip(),
        var rank: String = "",
        var coverImage: String = "",
        var subjectId: String = "",

        @MediaType
        var mediaType: String = MediaType.TYPE_ANIME,
        var mediaTypeName: String = ""
    ) : Parcelable

    @Keep
    @Parcelize
    data class InfoTip(
        var time: String = "",
        var eps: String = "",
        var fullTip: String = ""
    ) : Parcelable
}