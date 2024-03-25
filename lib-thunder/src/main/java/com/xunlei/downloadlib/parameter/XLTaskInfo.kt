@file:Suppress("SpellCheckingInspection")

package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class XLTaskInfo(
    @Keep @JvmField var mAddedHighSourceState: Int = 0,
    @Keep @JvmField var mAdditionalResCount: Int = 0,
    @Keep @JvmField var mAdditionalResDCDNBytes: Long = 0,
    @Keep @JvmField var mAdditionalResDCDNSpeed: Long = 0,
    @Keep @JvmField var mAdditionalResPeerBytes: Long = 0,
    @Keep @JvmField var mAdditionalResPeerSpeed: Long = 0,
    @Keep @JvmField var mAdditionalResType: Int = 0,
    @Keep @JvmField var mAdditionalResVipRecvBytes: Long = 0,
    @Keep @JvmField var mAdditionalResVipSpeed: Long = 0,
    @Keep @JvmField var mCheckedSize: Long = 0,
    @Keep @JvmField var mCid: String? = null,
    @Keep @JvmField var mDcdnState: Int = 0,
    @Keep @JvmField var mDownloadFileCount: Long = 0,
    @Keep @JvmField var mDownloadSize: Long = 0,
    @Keep @JvmField var mDownloadSpeed: Long = 0,
    @Keep @JvmField var mErrorCode: Int = 0,
    @Keep @JvmField var mFileName: String? = null,
    @Keep @JvmField var mFileSize: Long = 0,
    @Keep @JvmField var mGcid: String? = null,
    @Keep @JvmField var mInfoLen: Int = 0,
    @Keep @JvmField var mLanPeerResState: Int = 0,
    @Keep @JvmField var mOriginErrcode: Int = 0,
    @Keep @JvmField var mOriginRecvBytes: Long = 0,
    @Keep @JvmField var mOriginSpeed: Long = 0,
    @Keep @JvmField var mP2PRecvBytes: Long = 0,
    @Keep @JvmField var mP2PSpeed: Long = 0,
    @Keep @JvmField var mP2SRecvBytes: Long = 0,
    @Keep @JvmField var mP2SSpeed: Long = 0,
    @Keep @JvmField var mQueryIndexStatus: Int = 0,
    @Keep @JvmField var mTaskId: Long = 0,
    @Keep @JvmField @XLConstant.XLTaskStatus var mTaskStatus: Int = 0,
    @Keep @JvmField var mTotalFileCount: Long = 0,
) : Parcelable
