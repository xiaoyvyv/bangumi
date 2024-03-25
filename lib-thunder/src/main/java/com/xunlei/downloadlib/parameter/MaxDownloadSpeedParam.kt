package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MaxDownloadSpeedParam(
    @Keep @JvmField var mSpeed: Long = 0
) : Parcelable
