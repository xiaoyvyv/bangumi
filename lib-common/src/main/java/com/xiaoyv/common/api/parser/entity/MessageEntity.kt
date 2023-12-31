package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.MessageBoxType
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [MessageEntity]
 *
 * @author why
 * @since 12/9/23
 */
@Keep
@Parcelize
data class MessageEntity(
    override var id: String = "",
    var field: String = "",
    var fromId: String = "",
    var fromName: String = "",
    var fromAvatar: String = "",
    var mineAvatar: String = "",
    var subject: String = "",
    var summary: CharSequence = "",
    var time: String = "",
    var gh: String = "",
    var isUnRead: Boolean = false,
    @MessageBoxType
    var boxType: String = MessageBoxType.TYPE_INBOX,

    var isMine: Boolean = true,
    var isSending: Boolean = false,
) : IdEntity, Parcelable
