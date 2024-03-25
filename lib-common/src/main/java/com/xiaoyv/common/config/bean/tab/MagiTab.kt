package com.xiaoyv.common.config.bean.tab

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.MagiTabType
import kotlinx.parcelize.Parcelize

/**
 * Class: [MagiTab]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class MagiTab(
    var title: String,

    @MagiTabType
    var type: String
) : Parcelable