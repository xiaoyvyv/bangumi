package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

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
    var tags: String = "",
    var rank: String = "",
    var coverImage: String = "",
    @MediaType
    var searchMediaType: String = MediaType.TYPE_UNKNOWN,
    var searchTip: String = "",

    /**
     * API 搜索结果数据载体
     */
    var payload: @RawValue Any? = null,
) : IdEntity, Parcelable
