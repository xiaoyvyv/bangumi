package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [MessageCountEntity]
 *
 * @author why
 * @since 12/9/23
 */
@Keep
@Parcelize
data class MessageCountEntity(
    var unRead: Int = 0,
    var receiveCount: Int = 0,
    var sendCount: Int = 0,
) : Parcelable
