package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [TimelineDetailEntity]
 *
 * @author why
 * @since 1/1/24
 */
@Keep
@Parcelize
data class TimelineDetailEntity(
    override var id: String = "",
    var detail: TimelineEntity = TimelineEntity(),
    var replies: List<TimelineReplyEntity> = emptyList(),
) : Parcelable, IdEntity
