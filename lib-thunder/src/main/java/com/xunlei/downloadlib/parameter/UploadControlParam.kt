package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UploadControlParam(
    @Keep @JvmField var allowUploadNetWorkType: Int = 0,
    @Keep @JvmField var maxUploadBytes: Long = 0,
    @Keep @JvmField var maxUploadTime: Long = 0,
    @Keep @JvmField var uploadForNoTask: Boolean = false,
    @Keep @JvmField var uploadInterval: Long = 0
) : Parcelable
