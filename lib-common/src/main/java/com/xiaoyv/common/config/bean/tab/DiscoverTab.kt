package com.xiaoyv.common.config.bean.tab

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.DiscoverType
import kotlinx.parcelize.Parcelize

/**
 * Class: [DiscoverTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class DiscoverTab(
    var title: String,

    @DiscoverType
    var type: String
) : Parcelable