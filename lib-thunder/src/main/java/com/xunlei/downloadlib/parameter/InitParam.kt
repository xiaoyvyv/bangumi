package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class InitParam(
    @Keep @JvmField var mAppVersion: String? = null,
    @Keep @JvmField var mGuid: String? = null,
    @Keep @JvmField var mPeerId: String? = null,
    @Keep @JvmField var mLogSavePath: String? = null,
    @Keep @JvmField var mPermissionLevel: Int = 0,
    @Keep @JvmField var mStatCfgSavePath: String? = null,
    @Keep @JvmField var mStatSavePath: String? = null,
    @Keep @JvmField var mDebug: Boolean = false,
) : Parcelable {

    fun checkMemberVar(): Boolean {
        return mAppVersion != null && mGuid != null && mPeerId != null && mStatSavePath != null && mStatCfgSavePath != null && mLogSavePath != null
    }
}
