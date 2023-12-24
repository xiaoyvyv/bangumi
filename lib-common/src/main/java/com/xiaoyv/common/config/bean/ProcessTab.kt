package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.MediaType
import kotlinx.parcelize.Parcelize

/**
 * Class: [ProcessTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class ProcessTab(
    var title: String,
    @MediaType
    var type: String,
) : Parcelable