package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [IndexEntity]
 *
 * @author why
 * @since 12/12/23
 */
@Keep
@Parcelize
data class IndexEntity(
    var gridItems: List<Grid> = emptyList(),
    var newItems: List<IndexItemEntity> = emptyList(),
    var hotItems: List<IndexItemEntity> = emptyList(),
) : Parcelable {

    @Keep
    @Parcelize
    data class Grid(
        override var id: String = "",
        var images: List<String> = emptyList(),
        var title: String = "",
        var desc: String = "",
    ) : Parcelable, IdEntity
}
