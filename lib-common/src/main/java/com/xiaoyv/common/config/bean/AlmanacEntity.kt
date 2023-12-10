package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [AlmanacEntity]
 *
 * @author why
 * @since 12/10/23
 */
@Keep
@Parcelize
data class AlmanacEntity(
    override var id: String = "",
    var image: String = ""
) : Parcelable,IdEntity
