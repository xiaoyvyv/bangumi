package com.xiaoyv.common.api.response.base

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * BaseResponse
 *
 * @author why
 * @since 11/18/23
 */
@Keep
@Parcelize
open class BgmActionResponse<T>(
    val status: String? = "",
    val data: @RawValue T?,
) : Parcelable {

    val isOk: Boolean
        get() = status.orEmpty().equals("ok", true)
}
