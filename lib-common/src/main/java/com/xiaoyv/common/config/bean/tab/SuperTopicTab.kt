package com.xiaoyv.common.config.bean.tab

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.SuperType
import kotlinx.parcelize.Parcelize

/**
 * Class: [SuperTopicTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class SuperTopicTab(
    var title: String,
    @SuperType
    var type: String
) : Parcelable