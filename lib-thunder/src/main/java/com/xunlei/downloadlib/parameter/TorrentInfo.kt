package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class TorrentInfo(
    @Keep @JvmField var mFileCount: Int = 0,
    @Keep @JvmField var mInfoHash: String? = null,
    @Keep @JvmField var mIsMultiFiles: Boolean = false,
    @Keep @JvmField var mMultiFileBaseFolder: String? = null,
    @Keep @JvmField var mSubFileInfo: Array<TorrentFileInfo>? = null,
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TorrentInfo

        if (mFileCount != other.mFileCount) return false
        if (mInfoHash != other.mInfoHash) return false
        if (mIsMultiFiles != other.mIsMultiFiles) return false
        if (mMultiFileBaseFolder != other.mMultiFileBaseFolder) return false
        if (mSubFileInfo != null) {
            if (other.mSubFileInfo == null) return false
            if (!mSubFileInfo.contentEquals(other.mSubFileInfo)) return false
        } else if (other.mSubFileInfo != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mFileCount
        result = 31 * result + (mInfoHash?.hashCode() ?: 0)
        result = 31 * result + mIsMultiFiles.hashCode()
        result = 31 * result + (mMultiFileBaseFolder?.hashCode() ?: 0)
        result = 31 * result + (mSubFileInfo?.contentHashCode() ?: 0)
        return result
    }
}
