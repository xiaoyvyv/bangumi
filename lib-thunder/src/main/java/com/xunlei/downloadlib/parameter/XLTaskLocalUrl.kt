package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class XLTaskLocalUrl(
    @Keep
    @JvmField
    var mStrUrl: String? = null,
) : Parcelable
