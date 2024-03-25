package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class XLProductInfo(
    @Keep @JvmField var mProductKey: String? = null,
    @Keep @JvmField var mProductName: String? = null,
) : Parcelable
