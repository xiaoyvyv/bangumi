package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [FriendEntity]
 *
 * @author why
 * @since 12/14/23
 */
@Keep
@Parcelize
data class FriendEntity(
    override var id: String = "",
    var avatar: String = "",
    var name: String = "",
    var gh: String = "",
) : IdEntity, Parcelable
