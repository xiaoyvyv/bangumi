package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [EpSaveProgress]
 *
 * @author why
 * @since 12/18/23
 */
@Keep
@Parcelize
data class EpSaveProgress(
    var mediaId: String,
    var mediaName: String,
    var myProgress: Int,
    var totalProgress: Int,
) : Parcelable
