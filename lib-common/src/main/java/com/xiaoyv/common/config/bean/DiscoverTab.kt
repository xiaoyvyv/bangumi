package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.DiscoverType
import com.xiaoyv.common.config.annotation.ProfileType
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