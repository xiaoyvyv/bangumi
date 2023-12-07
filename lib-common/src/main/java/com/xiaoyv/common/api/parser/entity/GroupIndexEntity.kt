package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.bean.SampleAvatar
import kotlinx.parcelize.Parcelize

/**
 * Class: [GroupIndexEntity]
 *
 * @author why
 * @since 12/8/23
 */
@Keep
@Parcelize
data class GroupIndexEntity(
    var hotGroups: List<SampleAvatar> = emptyList(),
    var newGroups: List<SampleAvatar> = emptyList(),
    var hotTopics: List<TopicSampleEntity> = emptyList()
) : Parcelable
