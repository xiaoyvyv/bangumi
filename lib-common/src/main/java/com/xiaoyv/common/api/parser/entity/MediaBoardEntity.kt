package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [MediaBoardEntity]
 *
 * @author why
 * @since 11/29/23
 */
@Parcelize
@Keep
data class MediaBoardEntity(
    var id: String = "",
    var content: String = "",
    var userName: String = "",
    var userId: String = "",
    var time: String = "",
    var replies: String = "",
) : Parcelable
