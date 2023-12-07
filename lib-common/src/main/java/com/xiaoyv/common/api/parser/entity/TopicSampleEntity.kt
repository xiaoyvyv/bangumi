package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [TopicSampleEntity]
 *
 * @author why
 * @since 12/8/23
 */
@Keep
@Parcelize
data class TopicSampleEntity(
    override var id: String = "",
    var title: String = "",
    var userName: String = "",
    var userId: String = "",
    var groupName: String = "",
    var groupId: String = "",
    var time: String = "",
    var commentCount: Int = 0
) : IdEntity, Parcelable
