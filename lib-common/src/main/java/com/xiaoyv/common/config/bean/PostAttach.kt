package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [PostAttach]
 *
 * @author why
 * @since 12/6/23
 */
@Parcelize
@Keep
data class PostAttach(
    override var id: String = "",
    var image: String = "",
    var title: String = "",
    var type: String = "",
) : Parcelable, IdEntity