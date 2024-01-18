package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.ProfileType
import com.xiaoyv.common.config.annotation.SearchType
import kotlinx.parcelize.Parcelize

/**
 * Class: [SearchTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class SearchTab(
    var title: String,

    @SearchType
    var type: String
) : Parcelable