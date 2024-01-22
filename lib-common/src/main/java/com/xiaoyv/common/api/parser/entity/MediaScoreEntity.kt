package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [MediaScoreEntity]
 *
 * @author why
 * @since 1/22/24
 */
@Parcelize
@Keep
data class MediaScoreEntity(
    override var id: String = "",
    var time: String = "",
    var comment: String = "",
    var score: Float = 0f,
    var name: String = "",
    var avatar: String = "",
) : Parcelable, IdEntity
