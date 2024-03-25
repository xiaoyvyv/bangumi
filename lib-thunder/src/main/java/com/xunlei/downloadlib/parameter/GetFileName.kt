package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GetFileName(
    @Keep @JvmField var mFileName: String? = null,
) : Parcelable {

    fun getFileName(): String {
        return mFileName.orEmpty()
    }
}
