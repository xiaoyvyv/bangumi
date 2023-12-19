package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.bean.SampleImageEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [MonoEntity]
 *
 * @author why
 * @since 12/19/23
 */
@Keep
@Parcelize
data class MonoEntity(
    var grids: MutableList<Grid> = mutableListOf(),
) : Parcelable {

    @Keep
    @Parcelize
    data class Grid(
        var title: String = "",
        var items: List<SampleImageEntity> = emptyList(),
    ) : Parcelable
}
