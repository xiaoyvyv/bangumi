package com.xiaoyv.common.config.bean.tab

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.IndexTabCatType
import kotlinx.parcelize.Parcelize

/**
 * Class: [IndexDetailAttachTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class IndexDetailAttachTab(
    var title: String,
    @IndexTabCatType var type: String,
) : Parcelable