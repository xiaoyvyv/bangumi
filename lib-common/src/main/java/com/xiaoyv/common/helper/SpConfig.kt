package com.xiaoyv.common.helper

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.kts.SynchronizedLazyImpl

fun <T> lazyLiveSp(
    defaultValue: T,
    key: () -> String,
) = SynchronizedLazyImpl(initializer = { SpLiveData(defaultValue, key) })

class SpLiveData<T>(
    private val defaultValue: T,
    private val key: () -> String,
) : MutableLiveData<T>() {

    private val sharedPreferences get() = UserHelper.userSp

    private val requireKey get() = key()

    init {
        value = readSpValue()
    }

    override fun getValue(): T {
        return super.getValue() ?: readSpValue()
    }

    override fun setValue(value: T) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.setValue(value)
            saveSpValue(value)
        } else {
            postValue(value)
        }
    }

    override fun postValue(value: T) {
        super.postValue(value)
        saveSpValue(value)
    }

    private fun readSpValue(): T {
        val jsonString = sharedPreferences.getString(requireKey, null)
        return if (jsonString != null) {
            GsonUtils.getGson().fromJson(jsonString, object : TypeToken<T>() {}.type)
        } else {
            defaultValue
        }
    }

    private fun saveSpValue(value: T) {
        sharedPreferences.put(requireKey, value.toJson())
    }
}

