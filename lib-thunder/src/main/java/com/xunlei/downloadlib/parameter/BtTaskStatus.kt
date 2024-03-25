package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class BtTaskStatus(
    @Keep @JvmField var mStatus: IntArray,
) : Parcelable {
    constructor(length: Int) : this(IntArray(length))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BtTaskStatus

        return mStatus.contentEquals(other.mStatus)
    }

    override fun hashCode(): Int {
        return mStatus.contentHashCode()
    }
}


