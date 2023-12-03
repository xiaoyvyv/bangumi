package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [HomeIndexBannerEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Keep
@Parcelize
data class HomeIndexBannerEntity(
    var banners: List<String> = emptyList()
) : Parcelable