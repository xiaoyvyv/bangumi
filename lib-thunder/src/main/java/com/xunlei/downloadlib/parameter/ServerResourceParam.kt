package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ServerResourceParam(
    @Keep @JvmField var mComeFrom: Int = 0,
    @Keep @JvmField var mCookie: String? = null,
    @Keep @JvmField var mRefUrl: String? = null,
    @Keep @JvmField var mResType: Int = 0,
    @Keep @JvmField var mStrategy: Int = 0,
    @Keep @JvmField var mUrl: String? = null,
) : Parcelable {

    fun checkMemberVar(): Boolean {
        return mUrl != null
    }
}
