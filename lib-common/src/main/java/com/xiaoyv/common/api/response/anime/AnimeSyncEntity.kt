package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.database.subject.SubjectItem
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [AnimeSyncEntity]
 *
 * @author why
 * @since 1/26/24
 */
@Keep
@Parcelize
data class AnimeSyncEntity(
    override var id: String = "",
    var name: String = "",
    var nameCn: String = "",
    var comment: String = "",
    var summary: String = "",
    var image: String = "",
    var score: Double = 0.0,
    @InterestType
    var interestType: String = InterestType.TYPE_UNKNOWN,
    var interestText: String = "",
    var subject: List<SubjectItem> = emptyList(),
) : IdEntity, Parcelable
