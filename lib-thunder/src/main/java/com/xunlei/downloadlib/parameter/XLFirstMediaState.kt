package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class XLFirstMediaState(
    @Keep @JvmField var blockCount: Int = 0,
    @Keep @JvmField var disposeError: Int = 0,
    @Keep @JvmField var recvedCount: Int = 0,
    @Keep @JvmField var startRelateMs: Long = 0,
    @Keep @JvmField var state: Int = 0,
    @Keep @JvmField var stopRelateMs: Long = 0
) : Parcelable
