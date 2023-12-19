package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.TimelineType
import kotlinx.parcelize.Parcelize

/**
 * Class: [TimelineTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class TimelineTab(
    var title: String,
    @TimelineType
    var timelineType: String,
    var userId: String = ""
) : Parcelable