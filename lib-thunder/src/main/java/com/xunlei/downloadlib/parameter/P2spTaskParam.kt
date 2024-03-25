package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class P2spTaskParam(
    @Keep @JvmField var mCookie: String? = null,
    @Keep @JvmField var mCreateMode: Int = 0,
    @Keep @JvmField var mFileName: String? = null,
    @Keep @JvmField var mFilePath: String? = null,
    @Keep @JvmField var mPass: String? = null,
    @Keep @JvmField var mRefUrl: String? = null,
    @Keep @JvmField var mSeqId: Int = 0,
    @Keep @JvmField var mUrl: String? = null,
    @Keep @JvmField var mUser: String? = null
) : Parcelable {
    fun checkMemberVar(): Boolean {
        return mFileName != null && mFilePath != null && mUrl != null && mCookie != null && mRefUrl != null && mUser != null && mPass != null
    }

    fun setCookie(str: String) {
        mCookie = str
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

    fun setPass(str: String) {
        mPass = str
    }

    fun setRefUrl(str: String) {
        mRefUrl = str
    }

    fun setSeqId(i10: Int) {
        mSeqId = i10
    }

    fun setUrl(str: String) {
        mUrl = str
    }

    fun setUser(str: String) {
        mUser = str
    }
}
