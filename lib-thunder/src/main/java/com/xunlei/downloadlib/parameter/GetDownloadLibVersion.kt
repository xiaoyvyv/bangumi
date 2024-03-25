package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GetDownloadLibVersion(
    @Keep @JvmField var mVersion: String? = null,
) : Parcelable {

    fun getVersion(): String {
        return mVersion.orEmpty()
    }
}
