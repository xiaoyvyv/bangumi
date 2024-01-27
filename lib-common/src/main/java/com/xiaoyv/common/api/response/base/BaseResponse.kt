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
open class BaseResponse<T>(
    open val code: Int = 0,
    open val msg: String? = "",
    open val message: String? = "",
    open val data: @RawValue T? = null
) : Parcelable
