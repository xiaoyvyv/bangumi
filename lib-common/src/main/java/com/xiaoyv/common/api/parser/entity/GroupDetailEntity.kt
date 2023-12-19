package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.helper.callback.IdEntity
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
    var groupNumberId: String = "",
    var avatar: String = "",
    var name: String = "",
    var time: String = "",
    var summary: String = "",
    var summaryHtml: String = "",
    var recently: List<SampleImageEntity> = emptyList(),
    var otherGroups: List<SampleImageEntity> = emptyList(),
    var gh: String = "",
    var isJoin: Boolean = false,
) : IdEntity, Parcelable
