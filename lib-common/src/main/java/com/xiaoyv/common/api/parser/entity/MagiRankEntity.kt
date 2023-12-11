package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [MagiRankEntity]
 *
 * @author why
 * @since 12/11/23
 */
@Keep
@Parcelize
data class MagiRankEntity(
    var rateRank: List<MagiRank> = emptyList(),
    var createRank: List<MagiRank> = emptyList(),
) : Parcelable {

    @Keep
    @Parcelize
    data class MagiRank(
        override var id: String = "",
        var userName: String = "",
        var userAvatar: String = "",
        var correct: Int = 0,
        var correctMaxCount: Int = 0,
        var answered: Int = 0,
        var answeredMaxCount: Int = 0,
        var challenge: Boolean = false
    ) : Parcelable, IdEntity
}
