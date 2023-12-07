package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.bean.SampleAvatar
import com.xiaoyv.common.helper.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [GroupDetailEntity]
 *
 * @author why
 * @since 12/7/23
 */
@Parcelize
@Keep
data class GroupDetailEntity(
    override var id: String = "",
    var avatar: String = "",
    var name: String = "",
    var time: String = "",
    var summary: String = "",
    var summaryText: String = "",
    var recently: List<SampleAvatar> = emptyList(),
    var otherGroups: List<SampleAvatar> = emptyList()
) : IdEntity, Parcelable
