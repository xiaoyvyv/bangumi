package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class XLTaskInfoEx(
    @Keep @JvmField var mDcdnResConnSuccTotal: Int = 0,
    @Keep @JvmField var mDcdnResTotal: Int = 0,
    @Keep @JvmField var mOriginResConnSuccTotal: Int = 0,
    @Keep @JvmField var mOriginResTotal: Int = 0,
    @Keep @JvmField var mP2pResConnSuccTotal: Int = 0,
    @Keep @JvmField var mP2pResTotal: Int = 0,
    @Keep @JvmField var mServerResConnSuccTotal: Int = 0,
    @Keep @JvmField var mServerResTotal: Int = 0,
    @Keep @JvmField var mbtResConnSuccTotal: Int = 0,
    @Keep @JvmField var mbtResTotal: Int = 0,
) : Parcelable
