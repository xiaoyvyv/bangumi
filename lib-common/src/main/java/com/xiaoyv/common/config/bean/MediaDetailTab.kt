package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.DiscoverType
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.config.annotation.ProfileType
import kotlinx.parcelize.Parcelize

/**
 * Class: [MediaDetailTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class MediaDetailTab(
    var title: String,

    @MediaDetailType
    var type: String
) : Parcelable