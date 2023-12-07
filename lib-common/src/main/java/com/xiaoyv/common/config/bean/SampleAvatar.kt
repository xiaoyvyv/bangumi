package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.PersonBinderClickType
import com.xiaoyv.common.helper.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [SampleAvatar]
 *
 * @author why
 * @since 12/6/23
 */
@Parcelize
@Keep
data class SampleAvatar(
    override var id: String = "",
    var image: String = "",
    var title: String = "",
    var desc: String = "",
    @PersonBinderClickType var type: Int = 0
) : Parcelable, IdEntity