package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [BgmStatusEntity]
 *
 * @author why
 * @since 12/9/23
 */
@Keep
@Parcelize
data class BgmStatusEntity(
    @SerializedName("status")
    var status: String? = null
) : Parcelable