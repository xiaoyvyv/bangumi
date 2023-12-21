package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [FilterEntity]
 *
 * @author why
 * @since 12/21/23
 */
@Keep
@Parcelize
data class FilterEntity(
    override var id: String = "",
    var options: List<OptionItem> = emptyList(),
) : IdEntity, Parcelable {

    @Keep
    @Parcelize
    data class OptionItem(
        override var id: String = "",
        var title: String = "",
        var field: String = "",
        var selected: Boolean = false,
    ) : IdEntity, Parcelable
}
