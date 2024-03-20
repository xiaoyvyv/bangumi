package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [MikanEntity]
 *
 * @author why
 * @since 3/20/24
 */
@Keep
@Parcelize
data class MikanEntity(
    var id: String = "",
    var groups: List<Group>? = null,
) : Parcelable {
    @Keep
    @Parcelize
    data class Group(
        override var id: String = "",
        var mikanId: String = "",
        var name: String = "",
        var time: String = "",
        var poster: String = "",
    ) : Parcelable, IdEntity {
        /**
         * RSS
         */
        val rssUrl: String
            get() = "https://mikanime.tv/RSS/Bangumi?bangumiId=$mikanId&subgroupid=$id"
    }
}