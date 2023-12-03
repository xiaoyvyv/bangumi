package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class HomeIndexCardEntity(
    var title: String = "",
    var titleType: String = "",
    var images: List<BgmMediaEntity> = emptyList(),
) : Parcelable