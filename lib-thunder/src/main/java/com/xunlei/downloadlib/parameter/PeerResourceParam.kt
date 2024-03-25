package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PeerResourceParam(
    @Keep @JvmField var mCapabilityFlag: Int = 0,
    @Keep @JvmField var mInternalIp: Int = 0,
    @Keep @JvmField var mJmpKey: String? = null,
    @Keep @JvmField var mPeerId: String? = null,
    @Keep @JvmField var mResLevel: Int = 0,
    @Keep @JvmField var mResPriority: Int = 0,
    @Keep @JvmField var mResType: Int = 0,
    @Keep @JvmField var mTcpPort: Int = 0,
    @Keep @JvmField var mUdpPort: Int = 0,
    @Keep @JvmField var mUserId: Long = 0,
    @Keep @JvmField var mVipCdnAuth: String? = null,
) : Parcelable {

    fun checkMemberVar(): Boolean {
        return mPeerId != null && mJmpKey != null && mVipCdnAuth != null
    }

    companion object {
        const val PEER_RES_TYPE_DCDN: Int = 5
        const val PEER_RES_TYPE_LAN: Int = 8
        const val PEER_RES_TYPE_NORMAL: Int = 3
    }
}
