package com.xiaoyv.common.api.response
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName

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