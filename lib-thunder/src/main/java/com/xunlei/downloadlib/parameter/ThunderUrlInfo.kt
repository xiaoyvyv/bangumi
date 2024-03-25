package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ThunderUrlInfo(
    @Keep @JvmField var mUrl: String? = null
) : Parcelable
