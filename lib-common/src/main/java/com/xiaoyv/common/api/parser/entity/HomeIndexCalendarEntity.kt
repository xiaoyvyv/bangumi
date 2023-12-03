package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [HomeIndexCalendarEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Keep
@Parcelize
data class HomeIndexCalendarEntity(
    var tip: String = "",
    var today: List<BgmMediaEntity> = emptyList(),
    var tomorrow: List<BgmMediaEntity> = emptyList(),
) : Parcelable
