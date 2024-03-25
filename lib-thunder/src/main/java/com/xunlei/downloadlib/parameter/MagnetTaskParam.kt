package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MagnetTaskParam(
    @Keep @JvmField var mFileName: String? = null,
    @Keep @JvmField var mFilePath: String? = null,
    @Keep @JvmField var mUrl: String? = null,
) : Parcelable {
    fun checkMemberVar(): Boolean {
        return mFileName != null && mFilePath != null && mUrl != null
    }

    fun setFileName(str: String) {
        mFileName = str
    }

    fun setFilePath(str: String) {
        mFilePath = str
    }

    fun setUrl(str: String) {
        mUrl = str
    }
}
