package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [NotifyEntity]
 *
 * @author why
 * @since 12/8/23
 */
@Keep
@Parcelize
data class NotifyEntity(
    override var id: String = "",
    var userAvatar: String = "",
    var userName: String = "",
    var userId: String = "",
    var title: String = "",
    var titleLink: String = "",
    var targetId: String = "",
    var replyContent: CharSequence = "",
) : IdEntity, Parcelable