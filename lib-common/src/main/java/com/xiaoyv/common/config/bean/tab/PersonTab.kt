package com.xiaoyv.common.config.bean.tab

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.PersonTabType
import kotlinx.parcelize.Parcelize

/**
 * Class: [PersonTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class PersonTab(
    var title: String,

    @PersonTabType
    var type: Int
) : Parcelable