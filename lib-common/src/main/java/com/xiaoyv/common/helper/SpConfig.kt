package com.xiaoyv.common.helper

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.config.bean.WrapData
import com.xiaoyv.common.kts.SynchronizedLazyImpl

inline fun <reified T> lazyLiveSp(
    defaultValue: T,
    enable: Boolean = true,
    noinline key: () -> String,
) = SynchronizedLazyImpl(initializer = {
    if (enable) {
        SpLiveData(
            defaultValue = defaultValue,
            typeToken = object : TypeToken<WrapData<T>>() {},
            key = key
        )
    } else {
        SafeMutableLiveData(defaultValue)
    }
})

open class SafeMutableLiveData<T>(private val defaultValue: T) : MutableLiveData<T>(defaultValue) {
    override fun getValue(): T {
        return super.getValue() ?: defaultValue
    }
}

class SpLiveData<T>(
    private val defaultValue: T,
    private val typeToken: TypeToken<WrapData<T>>,
    private val key: () -> String,
) : SafeMutableLiveData<T>(defaultValue) {

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
            GsonUtils.getGson().fromJson(jsonString, typeToken)?.data ?: defaultValue
        } else {
            defaultValue
        }
    }

    private fun saveSpValue(value: T) {
        val toJson = WrapData(value).toJson()
        sharedPreferences.put(requireKey, toJson)
    }
}

