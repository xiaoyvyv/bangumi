package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class CIDTaskParam(
    @Keep @JvmField var mBcid: String? = null,
    @Keep @JvmField var mCid: String? = null,
    @Keep @JvmField var mCreateMode: Int = 0,
    @Keep @JvmField var mFileName: String? = null,
    @Keep @JvmField var mFilePath: String? = null,
    @Keep @JvmField var mFileSize: Long = 0,
    @Keep @JvmField var mGcid: String? = null,
    @Keep @JvmField var mSeqId: Int = 0
) : Parcelable {
    fun checkMemberVar(): Boolean {
        return mCid != null && mGcid != null && mBcid != null && mFilePath != null && mFileName != null
    }
}
