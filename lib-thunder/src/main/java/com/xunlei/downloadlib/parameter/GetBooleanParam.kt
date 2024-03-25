package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GetBooleanParam(
    @Keep @JvmField var mValue: Boolean = false,
) : Parcelable {

    fun getValue(): Boolean {
        return mValue
    }
}