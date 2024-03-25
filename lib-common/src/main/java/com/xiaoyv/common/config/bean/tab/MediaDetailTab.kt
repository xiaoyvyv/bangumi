package com.xiaoyv.common.config.bean.tab

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.MediaDetailType
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