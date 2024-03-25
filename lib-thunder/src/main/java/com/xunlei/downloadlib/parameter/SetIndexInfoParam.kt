package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SetIndexInfoParam(
    @Keep @JvmField var mBcid: String? = null,
    @Keep @JvmField var mCid: String? = null,
    @Keep @JvmField var mFileSize: Long = 0,
    @Keep @JvmField var mGcid: String? = null,
    @Keep @JvmField var mGcidLevel: Int = 0,
) : Parcelable {

    fun checkMemberVar(): Boolean {
        return mCid != null && mGcid != null && mBcid != null
    }
}
