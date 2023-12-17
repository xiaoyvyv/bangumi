package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [IndexAttachEntity]
 *
 * @author why
 * @since 12/17/23
 */
@Keep
@Parcelize
data class IndexAttachEntity(
    override var id: String = "",
    var title: String = "",
    var desc: String = "",
    var coverImage: String = "",
    var isCollection: Boolean = false,
    var comment: String = "",
    var no: Int = 0,
    @BgmPathType
    var pathType: String = BgmPathType.TYPE_UNKNOWN,
    @MediaType
    var mediaType: String = MediaType.TYPE_UNKNOWN,
) : IdEntity, Parcelable
