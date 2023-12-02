package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [MediaCommentEntity]
 *
 * @author why
 * @since 11/29/23
 */
@Parcelize
@Keep
data class MediaCommentEntity(
    var id: String = "",
    var avatar: String = "",
    var userName: String = "",
    var userId: String = "",
    var time: String = "",
    var comment: String = "",
    var star: Float = 0f
) : Parcelable
