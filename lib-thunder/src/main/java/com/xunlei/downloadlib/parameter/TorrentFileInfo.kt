package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class TorrentFileInfo(
    @Keep @JvmField var mFileIndex: Int = 0,
    @Keep @JvmField var mFileName: String? = null,
    @Keep @JvmField var mFileSize: Long = 0,
    @Keep @JvmField var mRealIndex: Int = 0,
    @Keep @JvmField var mSubPath: String? = null
) : Parcelable
