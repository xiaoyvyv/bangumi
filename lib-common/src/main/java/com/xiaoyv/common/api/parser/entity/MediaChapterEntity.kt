package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

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
    var titleCn: String = "",
    var titleNative: String = "",
    var finished: Boolean = false,
    var time: String = "",
    var commentCount: Int = 0,
    var stateText: String = "",
    var aired: Boolean = false,
) : Parcelable, IdEntity
