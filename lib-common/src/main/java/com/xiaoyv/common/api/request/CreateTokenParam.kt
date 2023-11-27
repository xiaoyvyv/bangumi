package com.xiaoyv.common.api.request

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [CreateTokenParam]
 *
 * @author why
 * @since 11/27/23
 */
@Keep
@Parcelize
data class CreateTokenParam(
    @SerializedName("name")
    var name: String = "Bangumi for Android",
    @SerializedName("duration_days")
    var durationDays: Int = 365
) : Parcelable