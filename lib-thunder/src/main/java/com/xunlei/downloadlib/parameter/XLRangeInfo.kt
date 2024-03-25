package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class XLRangeInfo(
    @Keep @JvmField var mRangeInfo: String? = null
) : Parcelable
