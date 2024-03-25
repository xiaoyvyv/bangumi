package com.xunlei.downloadlib.parameter

import androidx.annotation.Keep
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class BtTaskParam(
    @Keep @JvmField var mCreateMode: Int = 0,
    @Keep @JvmField var mFilePath: String? = null,
    @Keep @JvmField var mMaxConcurrent: Int = 0,
    @Keep @JvmField var mSeqId: Int = 0,
    @Keep @JvmField var mTorrentPath: String? = null
) : Parcelable {

    fun checkMemberVar(): Boolean {
        return mTorrentPath != null && mFilePath != null
    }

    fun setCreateMode(i10: Int) {
        mCreateMode = i10
    }

    fun setFilePath(str: String) {
        mFilePath = str
    }

    fun setMaxConcurrent(i10: Int) {
        mMaxConcurrent = i10
    }

    fun setSeqId(i10: Int) {
        mSeqId = i10
    }

    fun setTorrentPath(str: String) {
        mTorrentPath = str
    }
}
