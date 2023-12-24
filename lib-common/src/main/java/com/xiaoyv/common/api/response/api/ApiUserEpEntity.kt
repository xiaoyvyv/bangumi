package com.xiaoyv.common.api.response.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.EpCollectType
import kotlinx.parcelize.Parcelize


/**
 * Class: [ApiUserEpEntity]
 *
 * @author why
 * @since 12/24/23
 */
@Keep
@Parcelize
data class ApiUserEpEntity(
    @SerializedName("episode")
    var episode: ApiEpisodeEntity? = null,

    @EpCollectType @SerializedName("type")
    var type: Int = EpCollectType.TYPE_NONE,
) : Parcelable