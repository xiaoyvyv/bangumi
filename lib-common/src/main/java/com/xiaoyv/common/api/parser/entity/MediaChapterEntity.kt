package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MediaChapterPageEntity(
    override var id: String = "",
    var pages: List<MediaChapterEntity> = emptyList(),
) : Parcelable, IdEntity


/**
 * Class: [MediaChapterEntity]
 *
 * @author why
 * @since 11/29/23
 */
@Keep
@Parcelize
data class MediaChapterEntity(
    override var id: String = "",
    var mediaId: String = "",
    var titleCn: String = "",
    var titleNative: String = "",
    var time: String = "",
    var commentCount: Int = 0,
    var airedStateText: String = "",
    var isAired: Boolean = false,
    var isAiring: Boolean = false,
    @InterestType
    var collectType: String = InterestType.TYPE_UNKNOWN,
    var collectStateText: String = "",
    var epType: String = "",
    var number: String = "",

    var splitter: Boolean = false,
) : Parcelable, IdEntity


