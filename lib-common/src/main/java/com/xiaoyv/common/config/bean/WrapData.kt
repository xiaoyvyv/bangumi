package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Keep
@Parcelize
data class WrapData<T>(
    var data: @RawValue T? = null,
) : Parcelable