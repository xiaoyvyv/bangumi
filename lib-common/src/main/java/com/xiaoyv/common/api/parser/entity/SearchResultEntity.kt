package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [SearchResultEntity]
 *
 * @author why
 * @since 12/10/23
 */
@Parcelize
@Keep
data class SearchResultEntity(
    override var id: String = "",
    var title: String = "",
    var subtitle: String = "",
    var isCollection: Boolean = false,
    var count: String = "",
    var ratingScore: String = "",
    var rating: Float = 0f,
    var infoTip: BrowserEntity.InfoTip = BrowserEntity.InfoTip(),
    var rank: String = "",
    var coverImage: String = "",
    var searchType: String = ""
) : IdEntity, Parcelable
