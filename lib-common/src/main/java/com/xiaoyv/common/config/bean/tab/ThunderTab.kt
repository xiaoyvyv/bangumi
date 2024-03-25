package com.xiaoyv.common.config.bean.tab

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.DiscoverType
import com.xiaoyv.common.config.annotation.ThunderTabType
import kotlinx.parcelize.Parcelize

/**
 * Class: [ThunderTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class ThunderTab(
    var title: String,

    @ThunderTabType
    var type: String
) : Parcelable