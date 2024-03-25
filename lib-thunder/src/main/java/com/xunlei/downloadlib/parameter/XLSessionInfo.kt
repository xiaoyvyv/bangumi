package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class XLSessionInfo(
    @Keep @JvmField var mSendByte: Long = 0,
    @Keep @JvmField var mStartTime: Long = 0,
) : Parcelable
