package com.xunlei.downloadlib.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GetTaskId(
    @Keep @JvmField var mTaskId: Long = 0,
) : Parcelable {

    fun getTaskId(): Long {
        return mTaskId
    }
}
