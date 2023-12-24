package com.xiaoyv.common.api.request
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.EpCollectType

/**
 * Class: [EpCollectType]
 *
 * @author why
 * @since 12/24/23
 */
@Keep
@Parcelize
data class EpCollectParam(
    @EpCollectType
    @SerializedName("type")
    var type: Int = EpCollectType.TYPE_NONE
) : Parcelable