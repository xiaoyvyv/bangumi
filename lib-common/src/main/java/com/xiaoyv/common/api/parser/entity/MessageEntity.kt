package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.IdEntity
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
    var summary: CharSequence = "",
    var time: String = "",
    var deleteHash: String = "",
    var isMine: Boolean = true,
    var isSending: Boolean = false
) : IdEntity, Parcelable
