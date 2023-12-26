package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [BgmMediaEntity]
 */
@Keep
@Parcelize
data class BgmMediaEntity(
    var title: String = "",
    var attention: String = "",
    var image: String = "",
    var id: String = "",
) : Parcelable