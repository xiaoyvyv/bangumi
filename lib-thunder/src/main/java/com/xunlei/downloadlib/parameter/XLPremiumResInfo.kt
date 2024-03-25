package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class XLPremiumResInfo(
    @Keep @JvmField var mEmergency: Boolean = false,
    @Keep @JvmField var mPcdnBytes: Long = 0,
    @Keep @JvmField var mPcdnResCount: Int = 0,
    @Keep @JvmField var mPcdnResUsingCount: Int = 0,
    @Keep @JvmField var mPhubPremiumBytes: Long = 0,
    @Keep @JvmField var mPhubPremiumCount: Int = 0,
    @Keep @JvmField var mPhubPremiumUsingCount: Int = 0,
) : Parcelable
