package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.HomeFeatureType
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [HomeIndexFeature]
 *
 * @author why
 * @since 12/10/23
 */
@Keep
@Parcelize
data class HomeIndexFeature(
    @HomeFeatureType
    override var id: String,
    var title: String,
    var icon: Int,
    var sort: Int = 0
) : Parcelable, IdEntity
