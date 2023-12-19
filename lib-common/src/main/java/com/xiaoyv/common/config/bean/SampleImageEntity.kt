package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [SampleImageEntity]
 *
 * @author why
 * @since 12/6/23
 */
@Parcelize
@Keep
data class SampleImageEntity(
    override var id: String = "",
    var image: String = "",
    var title: String = "",
    var desc: String = "",
    var type: String = "",
) : Parcelable, IdEntity