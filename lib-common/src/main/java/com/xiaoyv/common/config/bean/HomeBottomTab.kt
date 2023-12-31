package com.xiaoyv.common.config.bean

import androidx.annotation.DrawableRes
import com.xiaoyv.common.config.annotation.FeatureType

/**
 * Class: [HomeBottomTab]
 *
 * @author why
 * @since 12/31/23
 */
data class HomeBottomTab(
    var title: String,
    var id: Int,
    @DrawableRes var icon: Int,
    @FeatureType var type: String,
)
