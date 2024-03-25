package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class BtIndexSet(
    @Keep @JvmField var mIndexSet: IntArray = intArrayOf(),
) : Parcelable {

    constructor(int: Int) : this(IntArray(int))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BtIndexSet

        return mIndexSet.contentEquals(other.mIndexSet)
    }

    override fun hashCode(): Int {
        return mIndexSet.contentHashCode()
    }
}
