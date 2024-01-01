package com.xiaoyv.common.api.request

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.EpCollectType
import kotlinx.parcelize.Parcelize

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
    var type: Int = EpCollectType.TYPE_NONE,
    @SerializedName("episode_id")
    var episodeId: List<Long>? = null,
) : Parcelable