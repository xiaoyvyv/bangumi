package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [NotifyEntity]
 *
 * @author why
 * @since 12/8/23
 */
@Keep
@Parcelize
data class NotifyEntity(
    @SerializedName("count")
    var count: Int? = null
) : Parcelable