package com.xiaoyv.anime.garden.api.response.base

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
    val code: Int = 0,
    val msg: String? = "",
    val data: @RawValue T? = null
) : Parcelable
