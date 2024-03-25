package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UploadInfo(
    @Keep @JvmField var maxUploadBytes: Long = 0,
    @Keep @JvmField var maxUploadTime: Long = 0,
    @Keep @JvmField var totalUploadBytes: Long = 0,
    @Keep @JvmField var totalUploadTimeCost: Long = 0,
    @Keep @JvmField var uploadInterval: Long = 0,
    @Keep @JvmField var uploadIntervalStart: Long = 0,
    @Keep @JvmField var uploadSpeed: Long = 0
) : Parcelable
