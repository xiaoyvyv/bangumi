package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class HLSTaskParam(
    @Keep @JvmField var mBandwidth: Long = 0,
    @Keep @JvmField var mCookie: String? = null,
    @Keep @JvmField var mCreateMode: Int = 0,
    @Keep @JvmField var mFileName: String? = null,
    @Keep @JvmField var mFilePath: String? = null,
    @Keep @JvmField var mMaxConcurrent: Int = 0,
    @Keep @JvmField var mPass: String? = null,
    @Keep @JvmField var mRefUrl: String? = null,
    @Keep @JvmField var mSeqId: Int = 0,
    @Keep @JvmField var mUrl: String? = null,
    @Keep @JvmField var mUser: String? = null,
) : Parcelable {

    fun checkMemberVar(): Boolean {
        return mUrl != null && mFilePath != null && mFileName != null
    }
}
