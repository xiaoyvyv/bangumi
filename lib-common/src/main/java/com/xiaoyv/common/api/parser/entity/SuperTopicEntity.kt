package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TopicTimeType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [SuperTopicEntity]
 *
 * @author why
 * @since 11/26/23
 */
@Keep
@Parcelize
data class SuperTopicEntity(
    override var id: String = "",
    var userName: String = "",
    var avatarId: String = "",
    var avatarUrl: String = "",
    var title: String = "",
    var attachId: String = "",
    var attachTitle: String = "",
    var time: String = "",
    var commentCount: Int = 0,
    @TopicType
    var topicType: String = TopicType.TYPE_UNKNOWN,
    @BgmPathType
    var pathType: String = BgmPathType.TYPE_UNKNOWN,
    @TopicTimeType
    var timeType: MutableList<String> = mutableListOf(),
) : IdEntity, Parcelable {
    val canShowActionMenu: Boolean
        get() = pathType == BgmPathType.TYPE_BLOG
}
