package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GetDownloadHead(
    @Keep @JvmField var mHttpResponse: String? = null,
    @Keep @JvmField var mHttpState: Int = -1,
) : Parcelable
