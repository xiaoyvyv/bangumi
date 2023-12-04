package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.UserCenterType
import kotlinx.parcelize.Parcelize

/**
 * Class: [UserCenterTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class UserCenterTab(
    var title: String,

    @UserCenterType
    var type: Int
) : Parcelable