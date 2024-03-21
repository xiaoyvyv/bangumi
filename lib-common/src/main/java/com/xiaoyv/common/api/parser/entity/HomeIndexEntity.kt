package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [HomeIndexEntity]
 *
 * @author why
 * @since 11/24/23
 */
@Keep
@Parcelize
data class HomeIndexEntity(
    var online: Int = 0,
    var banner: HomeIndexBannerEntity = HomeIndexBannerEntity(),
    var images: List<HomeIndexCardEntity> = emptyList(),
    var calendar: HomeIndexCalendarEntity = HomeIndexCalendarEntity(),
) : Parcelable