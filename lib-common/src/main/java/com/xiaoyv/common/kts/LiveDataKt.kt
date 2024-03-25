package com.xiaoyv.common.kts

import androidx.lifecycle.MutableLiveData

inline fun <reified T> MutableLiveData<T>.clear() {
    value = null
}