package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class BtSubTaskDetail(
    @Keep @JvmField var mFileIndex: Int = 0,
    @Keep @JvmField var mIsSelect: Boolean = false,
    @Keep @JvmField var mTaskInfo: XLTaskInfo = XLTaskInfo(),
) : Parcelable
