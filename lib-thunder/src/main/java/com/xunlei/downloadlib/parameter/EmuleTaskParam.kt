package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class EmuleTaskParam(
    @Keep @JvmField var mCreateMode: Int = 0,
    @Keep @JvmField var mFileName: String? = null,
    @Keep @JvmField var mFilePath: String? = null,
    @Keep @JvmField var mSeqId: Int = 0,
    @Keep @JvmField var mUrl: String? = null
) : Parcelable {

    fun checkMemberVar(): Boolean {
        return mFileName != null && mFilePath != null && mUrl != null
    }

    fun setCreateMode(i10: Int) {
        mCreateMode = i10
    }

    fun setFileName(str: String) {
        mFileName = str
    }

    fun setFilePath(str: String) {
        mFilePath = str
    }

    fun setSeqId(i10: Int) {
        mSeqId = i10
    }

    fun setUrl(str: String) {
        mUrl = str
    }
}
