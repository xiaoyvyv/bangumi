package com.xiaoyv.common.api.request

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [MicrosoftTranslateParam]
 *
 * @author why
 * @since 1/29/24
 */
@Keep
@Parcelize
data class MicrosoftTranslateParam(
    @SerializedName("Text")
    var text: String? = null,
) : Parcelable