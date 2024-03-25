package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UrlQuickInfo(
    @Keep @JvmField var mContentType: String? = null,
    @Keep @JvmField var mFileNameAdvice: String? = null,
    @Keep @JvmField var mFileSize: Long = 0,
    @Keep @JvmField @XLConstant.QuickInfoState var mState: Int = 0,
) : Parcelable
