package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.ProfileType
import kotlinx.parcelize.Parcelize

/**
 * Class: [ProfileTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class ProfileTab(
    var title: String,

    @ProfileType
    var type: String
) : Parcelable