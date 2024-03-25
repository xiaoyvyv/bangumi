package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class DcdnPeerResParam(
    @Keep @JvmField var nCapabilityFlag: Int = 0,
    @Keep @JvmField var nDownloadLength: Long = 0,
    @Keep @JvmField var nDownloadPos: Long = 0,
    @Keep @JvmField var nInternalIp: Int = 0,
    @Keep @JvmField var nResLevel: Int = 0,
    @Keep @JvmField var nResPriority: Int = 0,
    @Keep @JvmField var nTcpPort: Short = 0,
    @Keep @JvmField var nUdpPort: Short = 0,
    @Keep @JvmField var sFileName: String? = null,
    @Keep @JvmField var sPeerId: String? = null,
) : Parcelable
