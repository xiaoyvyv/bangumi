package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [BlockEntity]
 *
 * @author why
 * @since 12/17/23
 */
@Keep
@Parcelize
data class BlockEntity(
    override var id: String = "",
    var numberId: String = "",
    var name: String = "",
) : Parcelable, IdEntity
