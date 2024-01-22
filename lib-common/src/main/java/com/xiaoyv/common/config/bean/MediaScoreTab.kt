package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.ProfileType
import kotlinx.parcelize.Parcelize

/**
 * Class: [MediaScoreTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class MediaScoreTab(
    var title: String,

    @InterestType
    var type: String
) : Parcelable