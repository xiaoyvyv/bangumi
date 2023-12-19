package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.bean.SampleImageEntity
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
    var hotGroups: List<SampleImageEntity> = emptyList(),
    var newGroups: List<SampleImageEntity> = emptyList(),
    var hotTopics: List<TopicSampleEntity> = emptyList()
) : Parcelable
