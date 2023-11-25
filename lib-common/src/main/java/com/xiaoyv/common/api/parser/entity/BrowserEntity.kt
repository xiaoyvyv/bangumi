package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
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
    var options: List<Option> = emptyList(),
    var items: List<Item> = emptyList()
) : Parcelable {

    @Keep
    @Parcelize
    data class Item(
        var title: String = "",
        var isCollection: Boolean = false,
        var ratingCount: String = "",
        var ratingScore: String = "",
        var rating: String = "",
        var infoTip: InfoTip = InfoTip(),
        var rank: String = "",
        var titleCn: String = "",
        var coverImage: String = "",
        var subjectId: String = "",
    ) : Parcelable

    @Keep
    @Parcelize
    data class Option(
        var title: String = "",
        var items: List<OptionItem> = emptyList(),
    ) : Parcelable

    @Keep
    @Parcelize
    data class OptionItem(
        var path: String = "",
        var title: String = ""
    ) : Parcelable

    @Keep
    @Parcelize
    data class InfoTip(
        var time: String = "",
        var eps: String = ""
    ) : Parcelable
}