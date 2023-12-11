package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class IndexItemEntity(
    var id: String = "",
    var cover: String = "",
    var title: String = "",
    var desc: String = "",
    var userId: String = "",
    var userName: String = "",
    var userAvatar: String = "",
    var mediaType: String = "",
    var mediaCount: Int = 0,
    var time: String = "",
) : Parcelable