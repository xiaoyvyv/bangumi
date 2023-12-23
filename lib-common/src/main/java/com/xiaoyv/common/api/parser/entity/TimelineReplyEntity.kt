package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [TimelineReplyEntity]
 *
 * @author why
 * @since 12/23/23
 */
@Parcelize
@Keep
data class TimelineReplyEntity(
    override var id: String = "",
    var userName: String = "",
    var content: CharSequence = "",
    var contentHtml: String = "",
) : Parcelable, IdEntity