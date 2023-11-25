package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.MediaType
import kotlinx.parcelize.Parcelize

/**
 * Class: [MediaTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class MediaTab(
    var title: String,
    @MediaType
    var type: String
) : Parcelable